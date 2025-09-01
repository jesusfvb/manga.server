package com.manga.server.features.scrapper;

import java.util.List;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;

public interface Scraper {

   String baseURl();

   List<MangaModel> getNewChapter();

   List<MangaModel> searchMangas(String query);

   String getMangaDescription(String url);

   List<ChapterModel> getChapters(String url);

   List<ImgModel> gteImg(String url);
}
