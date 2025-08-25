package com.manga.server.scrapers;

import java.util.List;

import com.manga.server.models.ChapterModel;
import com.manga.server.models.ImgModel;
import com.manga.server.models.MangaModel;

public interface Scraper {

   String baseURl();

   List<MangaModel> getNewChapter();

   List<MangaModel> searchMangas(String query);

   String getMangaDescription(String url);

   List<ChapterModel> getChapters(String url);

   List<ImgModel> gteImg(String url);
}
