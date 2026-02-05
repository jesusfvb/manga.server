package com.manga.server.features.manga.user_cases;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Service
@RequiredArgsConstructor
@Log
public class MangaUpdateService {

    private final ScrapperService scrapperService;
//    private final SaveMangaUserCase saveMangaUserCase;

    @Qualifier("globalExecutor")
    private final Executor executor;

    // Control de concurrencia
    private final AtomicBoolean isUpdatingMangas = new AtomicBoolean(false);

    /**
     * Inicia la actualización de mangas con nuevos capítulos de forma asíncrona.
     */
    public void updateMangasWithNewChapters() {
        // Solo iniciar si no está ya ejecutándose
        if (isUpdatingMangas.compareAndSet(false, true)) {
            log.info("Iniciando actualización de mangas con nuevos capítulos");
            CompletableFuture.runAsync(this::processUpdate, executor)
                    .exceptionally(ex -> {
                        log.severe("Error en CompletableFuture de updateMangasWithNewChapters: " + ex.getMessage());
                        isUpdatingMangas.set(false);
                        return null;
                    });
        } else {
            log.info("La actualización de mangas con nuevos capítulos ya se está ejecutando, se omite la llamada");
        }
    }

    /**
     * Procesa la actualización de mangas con nuevos capítulos.
     */
    private void processUpdate() {
        try {
            var scrapper = ScrappersEnum.leerCapitulo;
            log.info("Ejecutando actualización de mangas con nuevos capítulos");

//            if (listOfMangasWhitNewChapterService.isTimeCheck(scrapper)) {
//                var mangasWhitNewChapters = scrapperService.getMangasWithNewChapters();
//                if (mangasWhitNewChapters != null && !mangasWhitNewChapters.isEmpty()) {
//                    mangaSaveService.saveIfNotExists(mangasWhitNewChapters);
//                    listOfMangasWhitNewChapterService.save(mangasWhitNewChapters, scrapper);
//                    log.info("Actualización completada: " + mangasWhitNewChapters.size() + " mangas procesados");
//                } else {
//                    log.warning("No se obtuvieron mangas del scrapper");
//                }
//            } else {
//                log.info("No se ha ejecutado la actualización porque ya se ejecutó en las últimas 30 minutos");
//            }
        } catch (Exception e) {
            log.severe("Error en processUpdate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isUpdatingMangas.set(false);
            log.info("Proceso de actualización de mangas finalizado");
        }
    }
}

