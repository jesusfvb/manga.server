package com.manga.server.features.scrapper.services;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.scrapers.leercapitulo.LeercapituloScraper;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScrapperService {
  final LeercapituloScraper leercapituloScraper;

  public List<MangaModel> getMangasWithNewChapters() {
    return leercapituloScraper.getMangasWithNewChapters();
  }

  public List<MangaModel> searchManga(ScrappersEnum scrapper, String query) {
    return switch (scrapper) {
      case leerCapitulo -> leercapituloScraper.searchMangas(query);
      default -> throw new IllegalArgumentException("Scraper not implemented: " + scrapper);
    };
  }

  public List<ChapterModel> getChapters(ScrappersEnum scrapper, String url) {
    return switch (scrapper) {
      case leerCapitulo -> leercapituloScraper.getChapters(url);
      default -> throw new IllegalArgumentException("Scraper not implemented: " + scrapper);
    };
  }

  public List<ImgModel> getImg(ScrappersEnum scrapper, String url) {
    return switch (scrapper) {
      case leerCapitulo -> leercapituloScraper.getImg(url);
      default -> throw new IllegalArgumentException("Scraper not implemented: " + scrapper);
    };
  }
}
