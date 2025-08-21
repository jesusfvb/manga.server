package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.MangaModel;
import com.manga.server.repository.MangaRepository;
import com.manga.server.scrapers.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class MangaService {

  final Scraper leerCapituloScraper;
  final MangaRepository mangaRepository;
  final NewListMangaService newListMangaService;

  public MangaModel getMangaById(String mangaId) {
    var mangaOptional = mangaRepository.findById(mangaId);
    if (mangaOptional.isPresent()) {
      return mangaOptional.get();
    } else {
      log.warning("Manga not found with id: " + mangaId);
      return null;
    }
  }

  public List<MangaModel> newMangas() {
    var scrapper = ScrappersEnum.leerCapitulo;
    if (newListMangaService.isTimeCheck(scrapper)) {
      var mangas = leerCapituloScraper.getNewChapter();
      exitOfSave(mangas);
      newListMangaService.saveListNewMangas(mangas, scrapper);
      log.info("Is new mangas for:" + scrapper);
      return mangas;
    } else {
      log.info("Is new mangas for: data base");
      return newListMangaService.getLastListNewManga(scrapper);
    }
  }

  public List<MangaModel> searchManga(String query) {
    ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name",
        ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

    var example = Example.of(MangaModel.builder().name(query).build(), matcher);
    var mangas = mangaRepository.findAll(example);
    var logMessage = "Is search for: data base";

    // TODO Actualizar este numero en un futuro
    if (mangas.size() <= 3) {
      var mangasScrapper = leerCapituloScraper.searchMangas(query);
      for (var magaScraper : mangasScrapper) {
        if (mangas.stream().noneMatch(m -> m.getName().equals(magaScraper.getName()))) {
          exitOfSave(magaScraper);
          mangas.add(magaScraper);
          logMessage = "Is search for:" + leerCapituloScraper.toString();
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
        var description = leerCapituloScraper.getMangaDescription(manga.getUrl());
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

}
