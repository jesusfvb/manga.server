package com.manga.server.core.browser;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
                    .setTimeout(30000)
            );
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
        if (e == null) return false;

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
