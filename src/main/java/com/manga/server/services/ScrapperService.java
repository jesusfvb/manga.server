package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.ChapterModel;
import com.manga.server.models.ImgModel;
import com.manga.server.models.MangaModel;
import com.manga.server.scrapers.LeercapituloScraper;
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
