package com.manga.server.features.manga.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

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

  private final Executor executor;

  private volatile boolean isUpdatingMangas = false;

  public void starApp() {
    CompletableFuture.runAsync(this::getMangasWithNewChapters, executor);
  }

  public List<MangaModel> getMangasByIds(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return mangaRepository.findAllById(ids);
  }

  public MangaModel getMangaById(String mangaId) {
    if (mangaId == null || mangaId.isEmpty()) {
      log.warning("getMangaById llamado con mangaId nulo o vacío");
      return null;
    }
    return mangaRepository.findById(mangaId).orElse(null);
  }

  public List<MangaModel> mangasWithNewChapters() {
    CompletableFuture.runAsync(this::getMangasWithNewChapters, executor);
    var scrapper = ScrappersEnum.leerCapitulo;
    var mangasWhitNewChapters = listOfMangasWhitNewChapterService.getLastListNewManga(scrapper);
    return mangasWhitNewChapters;
  }

  public List<MangaModel> searchManga(String query) {
    if (query == null || query.isEmpty()) {
      return List.of();
    }

    ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name",
        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

    // query ya está validado arriba, el builder es seguro
    @SuppressWarnings("null")
    var example = Example.of(MangaModel.builder().name(query).build(), matcher);
    var mangas = mangaRepository.findAll(example);
    var logMessage = "Is search for: data base";

    // TODO Actualizar este numero en un futuro
    if (mangas.size() <= 3) {
      var mangasScrapper = scrapperService.searchManga(ScrappersEnum.leerCapitulo, query);
      if (mangasScrapper != null) {
        for (var magaScraper : mangasScrapper) {
          if (magaScraper != null && mangas.stream().noneMatch(m -> 
              m != null && m.getName() != null && m.getName().equals(magaScraper.getName()))) {
            exitOfSave(magaScraper);
            mangas.add(magaScraper);
            logMessage = "Is search for:" + scrapperService.toString();
          }
        }
      }
    }
    log.info(logMessage);
    return mangas;
  }

  private void exitOfSave(MangaModel manga) {
    if (manga == null) {
      return;
    }
    var example = MangaModel.builder()
        .name(manga.getName() != null ? manga.getName() : "")
        .url(manga.getUrl() != null ? manga.getUrl() : "")
        .build();
    // El builder es seguro, los campos null son manejados correctamente por Spring Data
    @SuppressWarnings("null")
    var exit = mangaRepository.findOne(Example.of(example));
    if (exit.isEmpty()) {
      var savedManga = mangaRepository.save(manga);
      if (savedManga != null && savedManga.getId() != null) {
        manga.setId(savedManga.getId());
      }
    } else {
      var existingManga = exit.get();
      if (existingManga != null && existingManga.getId() != null) {
        manga.setId(existingManga.getId());
      }
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
      } else {
        log.info("No se ha ejecutado getMangasWithNewChapters porque ya se ha ejecutado en las últimas 30 minutos");
      }
    } catch (Exception e) {
      log.severe("Error en getMangasWithNewChapters: " + e.getMessage());
    } finally {
      isUpdatingMangas = false;
    }
  }
}
