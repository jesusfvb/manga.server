package com.manga.server.features.scrapper.scrapers.leercapitulo.helpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.manga.server.core.browser.PlaywrightManager;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;
import com.microsoft.playwright.ElementHandle;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Helper especializado en extracción de imágenes de capítulos.
 * Responsabilidad única: obtener imágenes usando Playwright y mapearlas a ImgModel.
 */
@Log
@Component
@RequiredArgsConstructor
public class ImagesScraperHelper {

  private final PlaywrightManager playwrightManager;


  public List<ImgModel> extractImages(String url, String baseUrl) {
    long startTime = System.currentTimeMillis();
    log.info("Iniciando extracción de imágenes - URL: " + url + ", Thread: " + Thread.currentThread().getName());

    final String finalUrl = url.contains(baseUrl) ? url : baseUrl + url;

    List<ImgModel> imgModels = new ArrayList<>();

    try {
      imgModels = playwrightManager.querySelectorAll(
          finalUrl,
          LeerCapituloSelectors.IMAGES_CONTAINER,
          LeerCapituloSelectors.DISPLAY_MODE_SCRIPT,
          this::mapElementToImage);

      log.info("Imágenes encontradas: " + imgModels.size());

      // Filtrar nulos y ordenar por número
      imgModels = imgModels.stream()
          .filter(Objects::nonNull)
          .sorted(Comparator.comparingInt(ImgModel::getNumber))
          .toList();

      long duration = System.currentTimeMillis() - startTime;
      log.info(String.format("Extracción completada - Imágenes procesadas: %d, Duración: %d ms",
          imgModels.size(), duration));

    } catch (IllegalStateException e) {
      long duration = System.currentTimeMillis() - startTime;
      if (e.getMessage() != null && e.getMessage().contains("state should be: open")) {
        log.severe(String.format(
            "ERROR: Contexto de Playwright cerrado o en estado inválido después de %d ms - URL: %s, Thread: %s",
            duration, finalUrl, Thread.currentThread().getName()));
      } else {
        log.severe("ERROR: Estado ilegal en extracción de imágenes después de " + duration + " ms - " + e.getMessage());
      }
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      log.severe(String.format("Error inesperado en extracción de imágenes después de %d ms - Tipo: %s, Mensaje: %s",
          duration, e.getClass().getSimpleName(), e.getMessage()));
    }

    log.info("Finalizando extracción - Retornando " + imgModels.size() + " imágenes");
    return imgModels;
  }
  
  public ImgModel mapElementToImage(ElementHandle element) {
    try {
      ElementHandle imgElement = element.querySelector("img");
      if (imgElement == null) {
        log.fine("No se encontró elemento img dentro del contenedor");
        return null;
      }

      String imgUrl = imgElement.getAttribute(LeerCapituloSelectors.IMAGE_ATTRIBUTE);
      if (imgUrl == null || imgUrl.isEmpty()) {
        log.fine("URL de imagen vacía o nula");
        return null;
      }

      String numberStr = element.getAttribute(LeerCapituloSelectors.IMAGE_NUMBER_ATTRIBUTE);
      if (numberStr == null || numberStr.isEmpty()) {
        log.fine("Número de imagen vacío o nulo");
        return null;
      }

      Integer number = Integer.parseInt(numberStr);

      return ImgModel.builder()
          .number(number)
          .scrapper(ScrappersEnum.leerCapitulo)
          .url(UrlModel.builder()
              .url(imgUrl)
              .scrapper(ScrappersEnum.leerCapitulo)
              .build())
          .lastUpdated(LocalDateTime.now())
          .build();

    } catch (NumberFormatException e) {
      log.fine("Error al parsear número de imagen: " + e.getMessage());
      return null;
    } catch (Exception e) {
      log.warning("Error al mapear elemento a ImgModel: " + e.getMessage());
      return null;
    }
  }
}



