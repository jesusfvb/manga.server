package com.manga.server.features.chapter.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Service
@RequiredArgsConstructor
public class ChapterService {


    private final ScrapperService scrapperService;
    private final ChapterRepository chapterRepository;

    public List<ChapterModel> getChapters(String mangaId) {
        if (mangaId == null || mangaId.isEmpty()) {
            log.warning("getChapters llamado con mangaId nulo o vacío");
            return List.of();
        }

        // TODO hacer que de aqui un tiempo se actualice la base de datos
        var chapters = chapterRepository.findByMangaIdOrderByNumberAsc(mangaId);
        var logMessage = "Is chapter for DataBase";
        if (chapters == null || chapters.isEmpty()) {
//            MangaModel manga = mangaService.getMangaById(mangaId);
//            if (manga == null || manga.getUrl() == null) {
//                log.warning("No se encontró el manga con ID: " + mangaId + " o no tiene URL");
//                return List.of();
//            }
////            var scrapedChapters = scrapperService.getChapters(manga.getUrl());
//            if (scrapedChapters != null && !scrapedChapters.isEmpty()) {
//                scrapedChapters.forEach(chapter -> {
//                    if (chapter != null) {
//                        chapter.setMangaId(mangaId);
//                        if (chapter.getLastUpdated() == null) {
//                            chapter.setLastUpdated(LocalDateTime.now());
//                        }
//                    }
//                });
//                chapters = chapterRepository.saveAll(scrapedChapters);
//                // Ordenar después de guardar ya que saveAll no garantiza orden
//                if (chapters != null && !chapters.isEmpty()) {
//                    chapters.sort(Comparator.comparingDouble(ChapterModel::getNumber));
//                }
//                logMessage = "Is chapter for Scrapper";
//            }
        }
        log.info(logMessage);
        // imgService.preLoadImages(chapters);
        return chapters != null ? chapters : List.of();
    }

    public ChapterModel getChapterById(String chapterId) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warning("getChapterById llamado con chapterId nulo o vacío");
            return null;
        }
        return chapterRepository.findById(chapterId).orElse(null);
    }

    public Double getLastChapterNumber(String mangaId) {
        if (mangaId == null || mangaId.isEmpty()) {
            return 0.0;
        }

        var chapters = chapterRepository.findByMangaIdOrderByNumberAsc(mangaId);
        if (chapters == null || chapters.isEmpty()) {
            chapters = getChapters(mangaId);
        }

        if (chapters == null || chapters.isEmpty()) {
            return 0.0;
        }

        return chapters.stream()
                .filter(chapter -> chapter != null && chapter.getNumber() != null)
                .max(Comparator.comparingDouble(ChapterModel::getNumber))
                .map(ChapterModel::getNumber)
                .orElse(0.0);
    }
}
