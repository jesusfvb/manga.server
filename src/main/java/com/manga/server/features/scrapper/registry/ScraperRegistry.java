package com.manga.server.features.scrapper.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.manga.server.features.scrapper.scrapers.Scraper;
import com.manga.server.shared.enums.ScrappersEnum;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;


@Component
@RequiredArgsConstructor
@Log
public class ScraperRegistry {

    private final List<Scraper> scrapers;
    private final Map<ScrappersEnum, Scraper> scraperMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Scraper scraper : scrapers) {
            ScrappersEnum scraperEnum = identifyScraper(scraper);
            if (scraperEnum != null) {
                scraperMap.put(scraperEnum, scraper);
                log.info("Registrado scraper: " + scraperEnum + " -> " + scraper.getClass().getSimpleName());
            } else {
                log.warning("No se pudo identificar el scraper: " + scraper.getClass().getSimpleName());
            }
        }
        log.info("ScraperRegistry inicializado con " + scraperMap.size() + " scrapers");
    }


    public Scraper getScraper(ScrappersEnum scraperEnum) {
        if (scraperEnum == null) {
            throw new IllegalArgumentException("Scraper enum no puede ser null");
        }
        
        Scraper scraper = scraperMap.get(scraperEnum);
        if (scraper == null) {
            throw new IllegalArgumentException("Scraper not implemented: " + scraperEnum);
        }
        
        return scraper;
    }


    private ScrappersEnum identifyScraper(Scraper scraper) {
        try {
            return scraper.getScrapperEnum();
        } catch (Exception e) {
            log.warning("Error al obtener ScrappersEnum del scraper " + scraper.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }
}

