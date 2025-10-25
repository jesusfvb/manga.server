package com.manga.server.features.scrapper.scrapers.leercapitulo;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manga.server.core.browser.PlaywrightManager;
import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.scrapers.Scraper;
import com.manga.server.features.scrapper.scrapers.leercapitulo.dtos.LeerCapituloSearchDTO;
import com.microsoft.playwright.Page;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@Service
@AllArgsConstructor
public class LeercapituloScraper implements Scraper {
  PlaywrightManager playwrightManager;

  @Override
  public String baseURl() {
    return "https://www.leercapitulo.co";
  }

  @Override
  public List<MangaModel> getMangasWithNewChapters() {

    List<MangaModel> mangas = new LinkedList<>();
    try {
      Document document = Jsoup.connect(baseURl()).get();

      Elements elements = document.select("body > section > div > div > div.col-md-8 > div > div > div");
      for (var element : elements) {
        String name = element.select("div.media-body > a > h4").text();
        String url = element.select("div.media-body > a").attr("href");
        String thumbnail = element.select("div > div.media-left.cover-manga > a > img").attr("data-src");
        String lastChapter = element.select("div.media-body > div > div > div > span:nth-child(1) > a")
            .text().replace("Capitulo", "").trim();

        mangas.add(MangaModel.builder().name(name).url(baseURl() + url).thumbnail(baseURl() + thumbnail)
            .lastChapter(Double.parseDouble(lastChapter)).build());
        buildManga(mangas);
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
        var mangaModel = MangaModel.builder().name(manga.label()).url(baseURl() + manga.link())
            .thumbnail(baseURl() + manga.thumbnail()).build();
        buildManga(mangaModel);
        mangas.add(mangaModel);
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mangas;
  }

  @Override
  public List<ChapterModel> getChapters(String url) {
    if (!url.contains(baseURl()))
      url = baseURl() + url;
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
  public List<ImgModel> getImg(String url) {
    final String finalUrl = url.contains(baseURl()) ? url : baseURl() + url;
    List<ImgModel> imgModels = new LinkedList<>();
    var context = playwrightManager.getContext();
    context.addInitScript("localStorage.setItem('display_mode', '1')");
    Page page = context.newPage();
    try {
      page.navigate(finalUrl);
      page.waitForLoadState();
      var images = page.querySelectorAll(".comic_wraCon > a");
      for (var image : images) {
        String img = image.querySelector("img").getAttribute("data-src");
        String number = image.getAttribute("name");
        if (img != null && !img.isEmpty()) {
          imgModels.add(ImgModel.builder().number(Integer.parseInt(number)).url(img).build());
        }
      }

    } catch (Exception e) {
      log.warning(e.getMessage());
    } finally {
      page.close();
    }

    if (imgModels.size() >= 1) {
      imgModels.sort(Comparator.comparingInt(ImgModel::getNumber));
    }
    return imgModels;
  }

  private void buildManga(List<MangaModel> mangaModelList) {
    mangaModelList.forEach((mangaModel) -> {
      buildManga(mangaModel);
    });
  }

  private void buildManga(MangaModel mangaModel) {
    var url = mangaModel.getUrl();
    if (!url.contains(baseURl()))
      url = baseURl() + url;
    try {
      Document document = Jsoup.connect(url).get();
      var description = getMangaDescription(document);

      mangaModel.setDescription(description);
      if (mangaModel.getLastChapter() == null) {
        var lastChapter = getLastChapter(document);
        mangaModel.setLastChapter(lastChapter);
      }
    } catch (IOException e) {

    }
  }

  private String getMangaDescription(Document document) {
    try {
      return document.select("#example2").text();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private Double getLastChapter(Document document) {
    try {
      var chapter = document.selectFirst("#examples > div > div > ul > li:nth-child(1) > div > h4 > a");
      String number = chapter.text().split(" ")[1];
      if (number.contains(":")) {
        number = number.split(":")[0];
      }
      return Double.parseDouble(number);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
