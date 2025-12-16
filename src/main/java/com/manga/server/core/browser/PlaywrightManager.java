package com.manga.server.core.browser;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.extern.java.Log;

@Component
@Log
public class PlaywrightManager implements DisposableBean {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    public PlaywrightManager() {
        // No inicializamos aquí — dejamos que Spring controle el ciclo de vida
    }

    /**
     * Inicializa Playwright después de que el contexto de Spring haya sido creado.
     */
    @PostConstruct
    private synchronized void initialize() {
        if (isInitialized.get()) {
            return;
        }

        try {
            log.info("Inicializando Playwright...");
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setTimeout(30000));
            context = browser.newContext();
            isInitialized.set(true);
            log.info("Playwright inicializado correctamente.");
        } catch (Exception e) {
            log.severe("Error al inicializar Playwright: " + e.getMessage());
            cleanup();
            throw new RuntimeException("No se pudo inicializar Playwright", e);
        }
    }

    /**
     * Obtiene el contexto de navegador actual.
     */
    public BrowserContext getContext() {
        if (isShuttingDown.get()) {
            throw new IllegalStateException("PlaywrightManager se está cerrando, no se pueden crear nuevos contextos");
        }

        if (!isInitialized.get() || context == null) {
            synchronized (this) {
                if (!isInitialized.get() || context == null) {
                    initialize();
                }
            }
        }

        return context;
    }

    public <T> List<T> querySelectorAll(@NonNull String url, @NonNull String selector, String initScript,
            @NonNull Function<ElementHandle, T> mapper) throws Exception {
        BrowserContext context = getContext();
        Page page = context.newPage();

        try {
            // Agregar script de inicialización a la página específica si se proporciona
            // Debe llamarse ANTES de navegar para que se ejecute durante la carga inicial
            if (initScript != null) {
                try {
                    page.addInitScript(initScript);
                    log.fine("Script de inicialización agregado: " + initScript);
                } catch (Exception e) {
                    log.warning(
                            "Error al agregar script de inicialización a la página: " + e.getMessage());
                    throw e;
                }
            }

            log.info("Navegando a URL: " + url);
            page.navigate(url);
            log.fine("Esperando a que la página cargue completamente");
            page.waitForLoadState();
            log.info("Página cargada exitosamente");

            // Esperar a que al menos un elemento con el selector esté disponible
            // Esto es importante para elementos que se cargan dinámicamente
            try {
                log.fine("Esperando a que los elementos con selector '" + selector + "' estén disponibles");
                page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
                log.fine("Elementos con selector disponibles");
            } catch (Exception e) {
                log.warning("No se encontraron elementos con selector '" + selector + "' después de esperar: "
                        + e.getMessage());
                // Continuar de todas formas, puede que no haya elementos
            }

            List<ElementHandle> elements = page.querySelectorAll(selector);
            log.info("Elementos encontrados: " + elements.size());

            List<T> mappedElements = elements.stream()
                    .map(mapper)
                    .collect(Collectors.toList());

            log.info("Elementos mapeados: " + mappedElements.size());
            return mappedElements;
        } catch (Exception e) {
            log.warning("Error al obtener elementos con selector: " + selector + " en la página: " + url + " - "
                    + e.getMessage());
            throw new Exception("Error al obtener elementos con selector: " + selector + " en la página: " + url + " - "
                    + e.getMessage());
        } finally {
            // Cerrar la página solo una vez en el finally para asegurar limpieza
            if (page != null && !page.isClosed()) {
                try {
                    page.close();
                } catch (Exception e) {
                    log.warning("Error al cerrar la página: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Método de cierre llamado automáticamente por Spring al destruir el bean.
     */
    @PreDestroy
    @Override
    public void destroy() {
        shutdown();
    }

    /**
     * Cierra Playwright y limpia recursos de forma segura.
     */
    private synchronized void shutdown() {
        if (isShuttingDown.getAndSet(true)) {
            // Ya se está cerrando
            return;
        }

        log.info("Iniciando cierre de Playwright (Thread: " + Thread.currentThread().getName() + ")");

        cleanup();

        log.info("Playwright cerrado correctamente.");
    }

    /**
     * Limpieza ordenada de recursos (browser → context → playwright)
     */
    private void cleanup() {
        if (browser != null) {
            try {
                log.fine("Cerrando navegador de Playwright...");
                browser.close();
                log.fine("Navegador cerrado correctamente.");
            } catch (Exception e) {
                handleExpectedShutdownException(e, "navegador");
            } finally {
                browser = null;
                context = null;
            }
        }

        // Pausa mínima para asegurar liberación de procesos nativos
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (playwright != null) {
            try {
                log.fine("Cerrando instancia de Playwright...");
                playwright.close();
                log.fine("Instancia de Playwright cerrada correctamente.");
            } catch (Exception e) {
                handleExpectedShutdownException(e, "instancia de Playwright");
            } finally {
                playwright = null;
            }
        }

        isInitialized.set(false);
        log.info("Proceso de limpieza de Playwright completado.");
    }

    /**
     * Maneja excepciones esperadas durante el cierre (como "Stream closed").
     */
    private void handleExpectedShutdownException(Throwable e, String source) {
        if (isExpectedShutdownException(e)) {
            log.fine("Excepción esperada durante el cierre de " + source + ": " + e.getMessage());
        } else {
            log.warning("Error inesperado al cerrar " + source + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica si una excepción es esperada durante el cierre de Playwright.
     */
    private boolean isExpectedShutdownException(Throwable e) {
        if (e == null)
            return false;

        String msg = e.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("stream closed") || lower.contains("pipe closed") || lower.contains("closed")) {
                return true;
            }
        }

        return (e instanceof java.io.IOException) || isExpectedShutdownException(e.getCause());
    }

    /**
     * Verifica si Playwright está disponible.
     */
    public boolean isAvailable() {
        return isInitialized.get() && !isShuttingDown.get()
                && playwright != null && browser != null && context != null;
    }
}
