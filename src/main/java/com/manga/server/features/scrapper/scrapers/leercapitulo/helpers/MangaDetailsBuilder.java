package com.manga.server.features.scrapper.scrapers.leercapitulo.helpers;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.features.manga.model.MangaModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Component
@RequiredArgsConstructor
public class MangaDetailsBuilder {

  private final JsoupWrapper jsoupWrapper;

  public void buildMangaDetails(MangaModel mangaModel, String baseUrl) {
    if (mangaModel == null || mangaModel.getUrl() == null) {
      log.warning("Intento de construir detalles con manga o URL nula");
      return;
    }

    var url = mangaModel.getUrl().getUrl();
    if (!url.contains(baseUrl)) {
      url = baseUrl + url;
    }

    try {
      Document document = jsoupWrapper.getDocument(url);

      // Obtener descripción
      String description = extractDescription(document);
      if (description != null && !description.isEmpty()) {
        mangaModel.setDescription(description);
        log.fine("Descripción obtenida: " + description.substring(0, Math.min(50, description.length())) + "...");
      }

      // Obtener último capítulo si no existe
      if (mangaModel.getLastChapter() == null) {
        Double lastChapter = extractLastChapterNumber(document);
        if (lastChapter != null) {
          mangaModel.setLastChapter(lastChapter);
          log.fine("Último capítulo obtenido: " + lastChapter);
        }
      }

    } catch (IOException e) {
      log.warning("Error de IO al construir detalles de manga: " + e.getMessage());
    } catch (Exception e) {
      log.warning("Error inesperado al construir detalles de manga: " + e.getMessage());
    }
  }


  private String extractDescription(Document document) {
    try {
      Element descriptionElement = document.selectFirst(LeerCapituloSelectors.MANGA_DESCRIPTION);
      if (descriptionElement != null) {
        String description = descriptionElement.text();
        return description != null && !description.isEmpty() ? description : null;
      }
    } catch (Exception e) {
      log.fine("Error al extraer descripción: " + e.getMessage());
    }
    return null;
  }


  private Double extractLastChapterNumber(Document document) {
    try {
      Element lastChapterElement = document.selectFirst(LeerCapituloSelectors.LAST_CHAPTER_ELEMENT);
      if (lastChapterElement != null) {
        String text = lastChapterElement.text();
        if (text != null && !text.isEmpty()) {
          return parseChapterNumber(text);
        }
      }
    } catch (Exception e) {
      log.fine("Error al extraer número del último capítulo: " + e.getMessage());
    }
    return null;
  }


  private Double parseChapterNumber(String text) {
    try {
      String[] parts = text.split(" ");
      if (parts.length < 2) {
        return null;
      }

      String numberPart = parts[1];
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


