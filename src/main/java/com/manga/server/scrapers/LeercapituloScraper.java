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
    return List.of(
        new MangaModel(1, "One Piece"),
        new MangaModel(2, "Naruto"),
        new MangaModel(3, "Bleach"),
        new MangaModel(4, "Attack on Titan"));
  }

}
