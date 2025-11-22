package com.manga.server.features.chapter.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.services.MangaService;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Log
@Service
public class ChapterService {

    @Lazy
    @Autowired
    private MangaService mangaService;
    @Autowired
    private ScrapperService scrapperService;

    @Lazy
    @Autowired
    private ImgService imgService;

    @Autowired
    ChapterRepository chapterRepository;

    public List<ChapterModel> getChapters(String mangaId) {
        if (mangaId == null || mangaId.isEmpty()) {
            log.warning("getChapters llamado con mangaId nulo o vacío");
            return List.of();
        }

        // TODO hacer que de aqui un tiempo se actualice la base de datos
        // mangaId ya está validado arriba, el builder es seguro
        @SuppressWarnings("null")
        var example = Example.of(ChapterModel.builder().mangaId(mangaId).build());
        var chapters = chapterRepository.findAll(example);
        var logMessage = "Is chapter for DataBase";
        if (chapters.isEmpty()) {
            MangaModel manga = mangaService.getMangaById(mangaId);
            if (manga == null || manga.getUrl() == null) {
                log.warning("No se encontró el manga con ID: " + mangaId + " o no tiene URL");
                return List.of();
            }
            var scrapedChapters = scrapperService.getChapters(ScrappersEnum.leerCapitulo, manga.getUrl());
            if (scrapedChapters != null && !scrapedChapters.isEmpty()) {
                scrapedChapters.forEach(chapter -> {
                    if (chapter != null) {
                        chapter.setMangaId(mangaId);
                        if (chapter.getLastUpdated() == null) {
                            chapter.setLastUpdated(LocalDateTime.now());
                        }
                    }
                });
                chapters = chapterRepository.saveAll(scrapedChapters);
                logMessage = "Is chapter for Scrapper";
            }
        }
        log.info(logMessage);
        if (chapters != null && !chapters.isEmpty()) {
            chapters.sort(Comparator.comparingDouble(ChapterModel::getNumber));
        }
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

        // mangaId ya está validado arriba, el builder es seguro
        @SuppressWarnings("null")
        var example = Example.of(ChapterModel.builder().mangaId(mangaId).build());
        var chapters = chapterRepository.findAll(example);
        if (chapters.isEmpty()) {
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
