package com.manga.server.features.scrapper.scrapers.leercapitulo.helpers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Component
@RequiredArgsConstructor
public class ChaptersScraperHelper {

  private final JsoupWrapper jsoupWrapper;

  public List<ChapterModel> extractChapters(String url, String baseUrl) {
    if (!url.contains(baseUrl)) {
      url = baseUrl + url;
    }

    try {
      Document document = jsoupWrapper.getDocument(url);
      return parseChaptersFromDocument(document, url);
    } catch (IOException e) {
      log.severe("Error de IO al extraer capítulos de: " + url + " - " + e.getMessage());
      return new ArrayList<>();
    } catch (Exception e) {
      log.severe("Error inesperado al extraer capítulos de: " + url + " - " + e.getMessage());
      return new ArrayList<>();
    }
  }


  private List<ChapterModel> parseChaptersFromDocument(Document document, String mangaUrl) {
    List<ChapterModel> chapterModels = new ArrayList<>();

    var chapters = document.select(LeerCapituloSelectors.CHAPTERS_LIST);

    for (Element chapter : chapters) {
      try {
        ChapterModel model = parseChapterElement(chapter, mangaUrl);
        if (model != null) {
          chapterModels.add(model);
          log.fine("Capítulo parseado: " + model.getNumber());
        }
      } catch (Exception e) {
        log.warning("Error al parsear elemento de capítulo: " + e.getMessage());
      }
    }

    log.info("Total de capítulos extraídos: " + chapterModels.size());
    return chapterModels;
  }


  private ChapterModel parseChapterElement(Element element, String mangaUrl) {
    String chapterUrl = element.attr(LeerCapituloSelectors.URL_ATTRIBUTE);
    String text = element.text();

    if (chapterUrl == null || chapterUrl.isEmpty() || text == null || text.isEmpty()) {
      log.warning("Elemento de capítulo con URL o texto vacío");
      return null;
    }

    Double chapterNumber = extractChapterNumber(text);
    if (chapterNumber == null) {
      log.warning("No se pudo extraer número de capítulo de: " + text);
      return null;
    }

    return ChapterModel.builder()
        .url(UrlModel.builder()
            .url(chapterUrl)
            .scrapper(ScrappersEnum.leerCapitulo)
            .build())
        .number(chapterNumber)
        .mangaId(mangaUrl)
        .lastUpdated(LocalDateTime.now())
        .build();
  }


  private Double extractChapterNumber(String text) {
    try {
      // Divide por espacios: "Capitulo X" o "Capitulo X: Título"
      String[] parts = text.split(" ");
      if (parts.length < 2) {
        return null;
      }

      String numberPart = parts[1];

      // Si contiene ":", tomar solo la parte antes
      if (numberPart.contains(LeerCapituloSelectors.CHAPTER_SEPARATOR)) {
        numberPart = numberPart.split(LeerCapituloSelectors.CHAPTER_SEPARATOR)[0];
      }

      return Double.parseDouble(numberPart);
    } catch (NumberFormatException e) {
      log.fine("No se pudo parsear número de capítulo: " + text);
      return null;
    }
  }
}


