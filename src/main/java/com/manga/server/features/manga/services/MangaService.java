package com.manga.server.features.manga.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.services.ChapterService;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Service
@RequiredArgsConstructor
@Log
public class MangaService {

  private final ScrapperService scrapperService;
  private final MangaRepository mangaRepository;
  private final ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;
  private final ChapterService chapterService;

  private final Executor executor;

  private volatile boolean isUpdatingMangas = false;

  public void starApp() {
    CompletableFuture.runAsync(this::getMangasWithNewChapters, executor);
  }

  public List<MangaModel> getMangasByIds(List<String> ids) {
    return mangaRepository.findAllById(ids);
  }

  public MangaModel getMangaById(String mangaId) {
    var mangaOptional = mangaRepository.findById(mangaId);
    if (mangaOptional.isPresent()) {
      return mangaOptional.get();
    } else {
      log.warning("Manga not found with id: " + mangaId);
      return null;
    }
  }

  public List<MangaModel> mangasWithNewChapters() {
    CompletableFuture.runAsync(this::getMangasWithNewChapters, executor);
    var scrapper = ScrappersEnum.leerCapitulo;
    var mangasWhitNewChapters = listOfMangasWhitNewChapterService.getLastListNewManga(scrapper);
    return mangasWhitNewChapters;
  }

  public List<MangaModel> searchManga(String query) {
    ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name",
        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

    var example = Example.of(MangaModel.builder().name(query).build(), matcher);
    var mangas = mangaRepository.findAll(example);
    var logMessage = "Is search for: data base";

    // TODO Actualizar este numero en un futuro
    if (mangas.size() <= 3) {
      var mangasScrapper = scrapperService.searchManga(ScrappersEnum.leerCapitulo, query);
      for (var magaScraper : mangasScrapper) {
        if (mangas.stream().noneMatch(m -> m.getName().equals(magaScraper.getName()))) {
          exitOfSave(magaScraper);
          mangas.add(magaScraper);
          logMessage = "Is search for:" + scrapperService.toString();
        }
        ;
      }
    }
    log.info(logMessage);
    return mangas;
  }

  public String mangaDescription(String id) {
    var mangaOptional = mangaRepository.findById(id);
    if (mangaOptional.isPresent()) {
      var manga = mangaOptional.get();
      if (manga.getDescription() == null) {
        var description = scrapperService.getMangaDescription(ScrappersEnum.leerCapitulo, manga.getUrl());
        manga.setDescription(description);
        mangaRepository.save(manga);
        log.info("Is description for:" + ScrappersEnum.leerCapitulo);
        return description;
      }
      log.info("Is description for: data base");
      return manga.getDescription();
    }
    return null;
  }

  private void exitOfSave(MangaModel manga) {
    var example = MangaModel.builder().name(manga.getName()).url(manga.getUrl()).build();
    var exit = mangaRepository.findOne(Example.of(example));
    if (exit.isEmpty()) {
      var id = mangaRepository.save(manga).getId();
      manga.setId(id);
    } else {
      manga.setId(exit.get().getId());
    }
  }

  private void exitOfSave(List<MangaModel> listManga) {
    for (var manga : listManga) {
      exitOfSave(manga);
    }
  }

  private void getMangasWithNewChapters() {
    if (isUpdatingMangas) {
      log.info("getMangasWithNewChapters ya se está ejecutando, se omite la llamada.");
      return;
    }
    isUpdatingMangas = true;
    try {
      var scrapper = ScrappersEnum.leerCapitulo;
      log.info("Ejecutando getMangasWithNewChapters");
      if (listOfMangasWhitNewChapterService.isTimeCheck(scrapper)) {
        var mangasWhitNewChapters = scrapperService.getMangasWithNewChapters();
        exitOfSave(mangasWhitNewChapters);
        listOfMangasWhitNewChapterService.save(mangasWhitNewChapters, scrapper);
        log.info("Ejecutado getMangasWithNewChapters");
      }
    } catch (Exception e) {
      log.severe("Error en getMangasWithNewChapters: " + e.getMessage());
    } finally {
      isUpdatingMangas = false;
    }
  }
}
