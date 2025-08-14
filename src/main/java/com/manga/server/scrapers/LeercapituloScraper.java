package com.manga.server.scrapers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.manga.server.models.MangaModel;

import lombok.extern.java.Log;

@Service
@Log
public class LeercapituloScraper implements Scraper {

    List<MangaModel> newMangasList = null;

    @Override
    public String baseURl() {
        return "https://leercapitulo.co";
    }

    @Override
    public List<MangaModel> getNewChapter() {

        if (newMangasList != null) {
            return newMangasList;
        }

        List<MangaModel> mangas = new LinkedList<>();
        try {
            Document document = Jsoup.connect(baseURl()).get();

            Elements elements = document.select("body > section > div > div > div.col-md-8 > div > div > div");
            for (var element : elements) {
                String name = element.select("div.media-body > a > h4").text();
                String url = element.select("div.media-body > a").attr("href");
                String thumbnail = element.select("div > div.media-left.cover-manga > a > img").attr("data-src");
                String numberOfChapters = element.select("div.media-body > div > div > div > span:nth-child(1) > a")
                        .text().replace("Capitulo", "").trim();
                mangas.add(new MangaModel(null, name, baseURl() + url, baseURl() + thumbnail,
                        Double.parseDouble(numberOfChapters)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        newMangasList = mangas;
        return mangas;
    }

}
