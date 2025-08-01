package com.manga.server.scrapers;

import java.util.List;

import com.manga.server.models.MangaModel;

public interface Scraper {

  public String baseURl();

  public List<MangaModel> getNewChapter();

}
