package com.manga.server.features.scrapper.scrapers;

import java.util.List;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.shared.enums.ScrappersEnum;

public interface Scraper {

  String baseURl();

  ScrappersEnum getScrapperEnum();

  List<MangaModel> getMangasWithNewChapters();

  List<MangaModel> searchMangas(String query);

  List<ChapterModel> getChapters(String url);

  List<ImgModel> getImg(String url);
}
