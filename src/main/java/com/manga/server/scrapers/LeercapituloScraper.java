package com.manga.server.scrapers;

import org.springframework.stereotype.Service;

@Service
public class LeercapituloScraper implements Scraper {

  @Override
  public String baseURl() {
    return "https://leercapitulo.co";
  }

  @Override
  public String getNewChapter() {
    return baseURl();
  }

}
