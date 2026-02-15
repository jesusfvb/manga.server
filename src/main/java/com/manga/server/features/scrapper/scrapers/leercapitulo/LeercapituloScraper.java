package com.manga.server.features.scrapper.scrapers.leercapitulo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.scrapers.Scraper;
import com.manga.server.features.scrapper.scrapers.leercapitulo.helpers.ChaptersScraperHelper;
import com.manga.server.features.scrapper.scrapers.leercapitulo.helpers.ImagesScraperHelper;
import com.manga.server.features.scrapper.scrapers.leercapitulo.helpers.MangasScraperHelper;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Service
@RequiredArgsConstructor
public class LeercapituloScraper implements Scraper {

  private final MangasScraperHelper mangasScraperHelper;
  private final ChaptersScraperHelper chaptersScraperHelper;
  private final ImagesScraperHelper imagesScraperHelper;

  private static final ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;

  @Override
  public String baseURl() {
    return scrapper.getUrl();
  }

  @Override
  public ScrappersEnum getScrapperEnum() {
    return scrapper;
  }

  @Override
  public List<MangaModel> getMangasWithNewChapters() {
    log.info("Orquestando extracción de mangas con nuevos capítulos");
    return mangasScraperHelper.extractMangasWithNewChapters(baseURl());
  }

  @Override
  public List<MangaModel> searchMangas(String query) {
    log.info("Orquestando búsqueda de mangas con query: " + query);
    return mangasScraperHelper.searchMangas(query, baseURl());
  }

  @Override
  public List<ChapterModel> getChapters(String url) {
    log.info("Orquestando extracción de capítulos de: " + url);
    return chaptersScraperHelper.extractChapters(url, baseURl());
  }

  @Override
  public List<ImgModel> getImg(String url) {
    log.info("Orquestando extracción de imágenes de: " + url);
    return imagesScraperHelper.extractImages(url, baseURl());
  }

}
