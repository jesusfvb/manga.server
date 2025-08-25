package com.manga.server.scrapers;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.manga.server.models.ChapterModel;
import com.manga.server.models.ImgModel;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manga.server.dtos.LeerCapituloSearchDTO;
import com.manga.server.models.MangaModel;

import lombok.extern.java.Log;

@Log
@Service
public class LeercapituloScraper implements Scraper {

    @Override
    public String baseURl() {
        return "https://www.leercapitulo.co";
    }

    @Override
    public List<MangaModel> getNewChapter() {

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

                mangas.add(MangaModel.builder().name(name).url(baseURl() + url).thumbnail(baseURl() + thumbnail)
                        .numberOfChapters(Double.parseDouble(numberOfChapters)).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mangas;
    }

    @Override
    public List<MangaModel> searchMangas(String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }
        List<MangaModel> mangas = new LinkedList<>();
        try {
            RestClient restClient = RestClient.create();
            String uri = baseURl() + "/search-autocomplete?term=" + query;
            String result = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            ObjectMapper objectMapper = new ObjectMapper();

            List<LeerCapituloSearchDTO> list = objectMapper.readValue(result,
                    new TypeReference<List<LeerCapituloSearchDTO>>() {
                    });
            list.forEach(manga -> {
                mangas.add(
                        MangaModel.builder().name(manga.label()).url(baseURl() + manga.link())
                                .thumbnail(baseURl() + manga.thumbnail()).build());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mangas;
    }

    @Override
    public String getMangaDescription(String url) {
        if (!url.contains(baseURl())) url = baseURl() + url;
        try {
            Document document = Jsoup.connect(url).get();
            return document.select("#example2").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChapterModel> getChapters(String url) {
        if (!url.contains(baseURl())) url = baseURl() + url;
        try {
            Document document = Jsoup.connect(url).get();
            var chapters = document.select("#examples > div > div > ul > li > div > h4 > a");
            List<ChapterModel> chapterModels = new LinkedList<>();
            for (var chapter : chapters) {
                String chapterUrl = chapter.attr("href");
                String number = chapter.text().split(" ")[1];

                if (number.contains(":")) {
                    number = number.split(":")[0];
                }
                chapterModels.add(ChapterModel.builder()
                        .url(baseURl() + chapterUrl)
                        .number(Double.parseDouble(number))
                        .mangaId(url)
                        .build());
            }
            return chapterModels;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ImgModel> gteImg(String url) {
        if (!url.contains(baseURl())) url = baseURl() + url;
        List<ImgModel> imgModels = new LinkedList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();
            context.addInitScript("localStorage.setItem('display_mode', '1')");
            Page page = context.newPage();
            page.navigate(url);
            page.waitForLoadState();
            var images = page.querySelectorAll(".comic_wraCon > a");
            for (var image : images) {
                String img = image.querySelector("img").getAttribute("data-src");
                String number = image.getAttribute("name");
                if (img != null && !img.isEmpty()) {
                    imgModels.add(ImgModel.builder().number(Integer.parseInt(number)).url(img).build());
                }
            }

            browser.close();
        }
        if(imgModels.size() >=1){
            imgModels.sort(Comparator.comparingInt(ImgModel::getNumber));
        }
        return imgModels;
    }

}
