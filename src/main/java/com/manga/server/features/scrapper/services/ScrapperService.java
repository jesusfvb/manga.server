package com.manga.server.features.scrapper.services;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.registry.ScraperRegistry;
import com.manga.server.features.scrapper.scrapers.Scraper;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapperService {
  
  private final ScraperRegistry scraperRegistry;

  public List<MangaModel> getMangasWithNewChapters() {
    Scraper scraper = scraperRegistry.getScraper(ScrappersEnum.leerCapitulo);
    return scraper.getMangasWithNewChapters();
  }


  public List<MangaModel> searchManga( String query) {
    Scraper scraper = scraperRegistry.getScraper(ScrappersEnum.leerCapitulo);
    return scraper.searchMangas(query);
  }


  public List<ChapterModel> getChapters(UrlModel url) {
    ScrappersEnum scrapper = url.getScrapper();
    Scraper scraper = scraperRegistry.getScraper(scrapper);
    return scraper.getChapters(url.getUrl());
  }

  public List<ImgModel> getImg(ScrappersEnum scrapper, String url) {
    Scraper scraper = scraperRegistry.getScraper(scrapper);
    return scraper.getImg(url);
  }
}
