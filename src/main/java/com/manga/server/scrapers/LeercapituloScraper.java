package com.manga.server.scrapers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.models.MangaModel;

@Service
public class LeercapituloScraper implements Scraper {

  @Override
  public String baseURl() {
    return "https://leercapitulo.co";
  }

  @Override
  public List<MangaModel> getNewChapter() {
    return List.of();
  }

}
