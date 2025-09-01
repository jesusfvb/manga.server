package com.manga.server.features.scrapper.services;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.scrapers.LeercapituloScraper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScrapperService {
    final LeercapituloScraper leercapituloScraper;

    public List<MangaModel> getNewMangas() {
        return leercapituloScraper.getNewChapter();
    }

    public List<MangaModel> searchManga(ScrappersEnum scrapper, String query) {
        return switch (scrapper) {
            case leerCapitulo -> leercapituloScraper.searchMangas(query);
            default -> throw new IllegalArgumentException("Scraper not implemented: " + scrapper);
        };
    }

    public String getMangaDescription(ScrappersEnum scrapper, String url) {
        return switch (scrapper) {
            case leerCapitulo -> leercapituloScraper.getMangaDescription(url);
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
            case leerCapitulo -> leercapituloScraper.gteImg(url);
            default -> throw new IllegalArgumentException("Scraper not implemented: " + scrapper);
        };
    }
}
