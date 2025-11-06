package com.manga.server.features.chapter.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgPreloadService {

    private final ImgService imgService;
    
    @Qualifier("globalExecutor")
    private final Executor executor;

    // Control de concurrencia
    private final ConcurrentHashMap<String, Boolean> pendingChapterIds = new ConcurrentHashMap<>();
    private final AtomicBoolean isPreloading = new AtomicBoolean(false);

    /**
     * Agrega capítulos a la cola de precarga y inicia el procesamiento si no está ya ejecutándose.
     * 
     * @param chapterIds Lista de IDs de capítulos a precargar
     */
    public void preloadImages(List<String> chapterIds) {
        int newChaptersCount = 0;
        if (chapterIds != null && !chapterIds.isEmpty()) {
            newChaptersCount = (int) chapterIds.stream()
                    .filter(id -> id != null && !id.isEmpty())
                    .peek(id -> pendingChapterIds.put(id, true))
                    .count();
        }

        // Iniciar procesamiento solo si hay IDs pendientes y no está ya ejecutándose
        if (!pendingChapterIds.isEmpty() && isPreloading.compareAndSet(false, true)) {
            log.info("Iniciando precarga de imágenes. Capítulos en cola: {} (nuevos: {})", 
                    pendingChapterIds.size(), newChaptersCount);
            // Ejecutar de forma asíncrona usando CompletableFuture nativo
            CompletableFuture.runAsync(this::processPreload, executor)
                    .exceptionally(ex -> {
                        log.error("Error en CompletableFuture de processPreload", ex);
                        isPreloading.set(false);
                        return null;
                    });
        } else if (isPreloading.get()) {
            log.debug("preloadImages llamado mientras ya se está ejecutando. " +
                    "Se agregaron {} capítulos a la cola. Total en cola: {}", 
                    newChaptersCount, pendingChapterIds.size());
        } else if (pendingChapterIds.isEmpty()) {
            log.debug("preloadImages llamado pero no hay capítulos pendientes para precargar");
        }
    }

    /**
     * Procesa la precarga de imágenes de forma asíncrona.
     * Usa CompletableFuture nativo de Java para manejar la concurrencia.
     */
    private void processPreload() {
        long startTime = System.currentTimeMillis();
        AtomicInteger processedCount = new AtomicInteger(0);
        
        try {
            // Crear snapshot y limpiar los IDs pendientes antes de procesar
            // para evitar que nuevos IDs se agreguen durante el procesamiento
            var snapshot = List.copyOf(pendingChapterIds.keySet());
            pendingChapterIds.clear();

            if (snapshot.isEmpty()) {
                log.info("Finalizando precarga: no hay capítulos para procesar");
                isPreloading.set(false);
                return;
            }

            log.info("Procesando precarga de imágenes de {} capítulos...", snapshot.size());

            // Procesar cada capítulo secuencialmente para evitar sobrecarga
            snapshot.forEach(chapterId -> {
                try {
                    imgService.getImg(chapterId);
                    processedCount.incrementAndGet();
                } catch (Exception e) {
                    log.warn("Error al pre-cargar imágenes para chapterId {}: {}", chapterId, e.getMessage(), e);
                }
            });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Pre-carga finalizada exitosamente. Capítulos procesados: {}/{}. Duración: {} ms", 
                    processedCount.get(), snapshot.size(), duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error en processPreload después de {} ms. Capítulos procesados: {}", 
                    duration, processedCount.get(), e);
        } finally {
            isPreloading.set(false);
            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("Precarga finalizada. Duración total: {} ms. Estado: {}", 
                    totalDuration, isPreloading.get() ? "en ejecución" : "completada");

            // Si hay nuevos IDs agregados después del clear, procesarlos
            // Usar CompletableFuture para ejecutar de forma asíncrona
            if (!pendingChapterIds.isEmpty()) {
                log.info("Nuevos capítulos detectados tras la precarga ({} en cola). Relanzando...", 
                        pendingChapterIds.size());
                // Relanzar usando CompletableFuture nativo
                if (isPreloading.compareAndSet(false, true)) {
                    CompletableFuture.runAsync(this::processPreload, executor)
                            .exceptionally(ex -> {
                                log.error("Error en CompletableFuture de relanzamiento de processPreload", ex);
                                isPreloading.set(false);
                                return null;
                            });
                } else {
                    // Si no se pudo adquirir el lock, simplemente agregar a la cola
                    // y esperar a que el proceso actual termine
                    log.debug("No se pudo relanzar inmediatamente, los capítulos quedan en cola");
                }
            }
        }
    }
}

