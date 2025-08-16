package com.manga.server.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.models.MangaModel;
import com.manga.server.repository.MangaRepository;
import com.manga.server.scrapers.Scraper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MangaService {

  final Scraper leerCapituloScraper;
  final MangaRepository mangaRepository;

  public List<MangaModel> newMangas() {
    var scraperMangas = leerCapituloScraper.getNewChapter();
    exitOfSave(scraperMangas);
    return List.of();
  };

  public List<MangaModel> searchManga(String query) {
    List<MangaModel> mangas = new LinkedList<>();

    return mangas;
  };

  private void exitOfSave(List<MangaModel> listManga) {
    for (var manga : listManga) {
      var exmaple = new MangaModel(null, manga.getName(), manga.getUrl(), null, null);
      var exit = mangaRepository.exists(Example.of(exmaple));
      if (!exit) {
        manga = mangaRepository.save(manga);
      }
    }
  }
}
