package com.manga.server.features.manga.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Service
@RequiredArgsConstructor
@Log
public class MangaService {

  private final ScrapperService scrapperService;
  private final MangaRepository mangaRepository;
  private final ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;
  private final MangaSaveService mangaSaveService;

  @Lazy
  @Autowired
  private MangaUpdateService mangaUpdateService;

  public void starApp() {
    mangaUpdateService.updateMangasWithNewChapters();
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
    mangaUpdateService.updateMangasWithNewChapters();
    var scrapper = ScrappersEnum.leerCapitulo;
    var mangasWhitNewChapters = listOfMangasWhitNewChapterService.getLastListNewManga(scrapper);
    return mangasWhitNewChapters != null ? mangasWhitNewChapters : List.of();
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
            mangaSaveService.saveIfNotExists(magaScraper);
            mangas.add(magaScraper);
            logMessage = "Is search for:" + scrapperService.toString();
          }
        }
      }
    }
    log.info(logMessage);
    return mangas;
  }

}
