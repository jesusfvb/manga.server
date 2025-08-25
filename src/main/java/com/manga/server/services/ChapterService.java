package com.manga.server.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.manga.server.dtos.ImgDTO;
import com.manga.server.enums.ScrappersEnum;
import lombok.extern.java.Log;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.models.ChapterModel;
import com.manga.server.models.MangaModel;
import com.manga.server.repository.ChapterRepository;

import lombok.AllArgsConstructor;

@Log
@Service
@AllArgsConstructor
public class ChapterService {

    final MangaService mangaService;
    final ScrapperService scrapperService;

    final ChapterRepository chapterRepository;

    public List<ChapterModel> getChapters(String mangaId) {
        // TODO hacer que de aqui un tiempo se actualice la base de datos
        var example = Example.of(ChapterModel.builder().mangaId(mangaId).build());
        var chapters = chapterRepository.findAll(example);
        var logMessage = "Is chapter for DataBase";
        if (chapters.isEmpty()) {
            MangaModel manga = mangaService.getMangaById(mangaId);
            chapters = scrapperService.getChapters(ScrappersEnum.leerCapitulo, manga.getUrl());
            chapters.forEach(chapter -> chapter.setMangaId(mangaId));
            chapters = chapterRepository.saveAll(chapters);
            logMessage = "Is chapter for Scrapper";
        }
        log.info(logMessage);
        chapters.sort(Comparator.comparingDouble(ChapterModel::getNumber));
        return chapters;
    }

    public ChapterModel getChapterById(String chapterId) {
//      TODO manejar el error
        return chapterRepository.findById(chapterId).orElseThrow();
    }
}
