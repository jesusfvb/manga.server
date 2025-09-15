package com.manga.server.features.chapter.services;

import java.util.Comparator;
import java.util.List;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.services.MangaService;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Log
@Service
public class ChapterService {

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
    // imgService.preLoadImages(chapters);
    return chapters;
  }

  public ChapterModel getChapterById(String chapterId) {
    // TODO manejar el error
    return chapterRepository.findById(chapterId).orElseThrow();
  }
}
