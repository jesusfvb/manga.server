package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.ChapterModel;
import com.manga.server.scrapers.LeercapituloScraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO poner esto para el servicio de manga también

@Service
@AllArgsConstructor
public class ScrapperService {
    final LeercapituloScraper leercapituloScraper;

    public List<ChapterModel> getChapters(ScrappersEnum scrappers, String url) {
        return switch (scrappers) {
            case leerCapitulo -> leercapituloScraper.getChapters(url);
            default -> throw new IllegalArgumentException("Scraper not implemented: " + scrappers);
        };
    }
}
