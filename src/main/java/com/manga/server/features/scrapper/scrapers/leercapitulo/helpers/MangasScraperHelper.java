package com.manga.server.features.scrapper.scrapers.leercapitulo.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.core.browser.RestClientWrapper;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.scrapers.leercapitulo.dtos.LeerCapituloSearchDTO;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;


@Log
@Component
@RequiredArgsConstructor
public class MangasScraperHelper {

  private final JsoupWrapper jsoupWrapper;
  private final RestClientWrapper restClientWrapper;
  private final MangaDetailsBuilder mangaDetailsBuilder;

  public List<MangaModel> extractMangasWithNewChapters(String baseUrl) {
    long startTime = System.currentTimeMillis();
    log.info("Iniciando extracción de mangas con nuevos capítulos - Thread: " + Thread.currentThread().getName());

    List<MangaModel> mangas = new ArrayList<>();

    try {
      extractMangas(baseUrl, mangas);
    } catch (Exception e) {
      handleExtractionError(startTime, e);
    }

    return mangas;
  }

  /**
   * Maneja errores en la extracción con logging apropiado
   */
  private void handleExtractionError(long startTime, Exception e) {
    long duration = System.currentTimeMillis() - startTime;

    if (e instanceof IOException) {
      log.severe("Error de IO después de " + duration + " ms: " + e.getMessage());
    } else {
      log.severe("Error inesperado después de " + duration + " ms: " + e.getMessage());
    }
  }

  /**
   * Obtiene el documento HTML y extrae los elementos de manga.
   *
   * @param baseUrl URL base para conectar
   * @param mangas Lista donde agregar los mangas extraídos
   * @throws IOException Si hay error de IO al obtener documento
   */
  private void extractMangas(String baseUrl, List<MangaModel> mangas) throws IOException {
    long startTime = System.currentTimeMillis();

    log.info("Conectando a: " + baseUrl);
    Document document = jsoupWrapper.getDocument(baseUrl);
    log.info("Conexión exitosa");

    Elements mangaElements = document.select(LeerCapituloSelectors.MANGA_CONTAINER);
    log.info("Elementos de manga encontrados: " + mangaElements.size());

    ProcessingStats stats = processMangaElements(mangaElements, mangas, baseUrl);

    long duration = System.currentTimeMillis() - startTime;
    log.info(String.format(
        "Extracción completada - Mangas procesados: %d, Errores: %d, Total: %d, Duración: %d ms",
        stats.processedCount, stats.errorCount, mangas.size(), duration));
  }
  public List<MangaModel> searchMangas(String query, String baseUrl) {
    if (query == null || query.isEmpty()) {
      return List.of();
    }

    List<MangaModel> mangas = new ArrayList<>();

    try {
      String uri = baseUrl + LeerCapituloSelectors.SEARCH_ENDPOINT + query;
      log.info("Buscando mangas con query: " + query + " - URL: " + uri);

      List<LeerCapituloSearchDTO> searchResults = restClientWrapper.get(uri,
          new TypeReference<>() {
          });

      if (searchResults == null || searchResults.isEmpty()) {
        log.info("No se encontraron resultados de búsqueda");
        return mangas;
      }

      log.info("Resultados de búsqueda obtenidos: " + searchResults.size());

      processSearchResults(searchResults, mangas, baseUrl);

      log.info("Total de mangas procesados de búsqueda: " + mangas.size());

    } catch (Exception e) {
      log.severe("Error en búsqueda de mangas: " + e.getMessage());
    }

    return mangas;
  }


  private MangaModel parseMangaElement(Element element) {
    String name = element.select(LeerCapituloSelectors.MANGA_NAME).text();
    Element urlElement = element.select(LeerCapituloSelectors.MANGA_URL).first();
    String url = urlElement != null ? urlElement.attr(LeerCapituloSelectors.URL_ATTRIBUTE) : "";
    String thumbnail = element.select(LeerCapituloSelectors.MANGA_THUMBNAIL)
        .attr(LeerCapituloSelectors.THUMBNAIL_ATTRIBUTE);
    String lastChapterText = element.select(LeerCapituloSelectors.MANGA_LAST_CHAPTER).text()
        .replace(LeerCapituloSelectors.CHAPTER_PREFIX, "").trim();

    // Validaciones
    if (name.isEmpty()) {
      log.warning("Manga sin nombre encontrado");
      return null;
    }

    if (url.isEmpty()) {
      log.warning("Manga '" + name + "' sin URL");
      return null;
    }

    Double lastChapter = null;
    if (!lastChapterText.isEmpty()) {
      try {
        lastChapter = Double.parseDouble(lastChapterText);
      } catch (NumberFormatException e) {
        log.fine("No se pudo parsear último capítulo para '" + name + "': " + lastChapterText);
      }
    }

    return MangaModel.builder()
        .name(name)
        .url(UrlModel.builder()
            .url(url)
            .scrapper(ScrappersEnum.leerCapitulo)
            .build())
        .thumbnail(UrlModel.builder()
            .url(thumbnail)
            .scrapper(ScrappersEnum.leerCapitulo)
            .build())
        .lastChapter(lastChapter)
        .build();
  }

  private void processSearchResults(List<LeerCapituloSearchDTO> searchResults, List<MangaModel> mangas, String baseUrl) {
    for (LeerCapituloSearchDTO dto : searchResults) {
      processSearchResult(dto, mangas, baseUrl);
    }
  }


  private void processSearchResult(LeerCapituloSearchDTO dto, List<MangaModel> mangas, String baseUrl) {
    try {
      MangaModel manga = convertSearchDTOToMangaModel(dto);
      if (manga != null) {
        // Construir detalles adicionales
        mangaDetailsBuilder.buildMangaDetails(manga, baseUrl);
        mangas.add(manga);
        log.fine("Manga de búsqueda agregado: " + manga.getName());
      }
    } catch (Exception e) {
      log.warning("Error al convertir resultado de búsqueda: " + e.getMessage());
    }
  }


  private MangaModel convertSearchDTOToMangaModel(LeerCapituloSearchDTO dto) {
    // Validar que todos los campos requeridos no sean nulos o vacíos
    if (dto.link() == null || dto.link().isEmpty() ||
        dto.label() == null || dto.label().isEmpty() ||
        dto.thumbnail() == null || dto.thumbnail().isEmpty() ||
        dto.value() == null || dto.value().isEmpty()) {
      log.fine("DTO de búsqueda con campos vacíos, omitiendo");
      return null;
    }

    return MangaModel.builder()
        .name(dto.label())
        .url(UrlModel.builder()
            .url(dto.link())
            .scrapper(ScrappersEnum.leerCapitulo)
            .build())
        .thumbnail(UrlModel.builder()
            .url(dto.thumbnail())
            .scrapper(ScrappersEnum.leerCapitulo)
            .build())
        .build();
  }


  private ProcessingStats processMangaElements(Elements mangaElements, List<MangaModel> mangas, String baseUrl) {
    int processedCount = 0;
    int errorCount = 0;

    for (Element element : mangaElements) {
      try {
        processSingleMangaElement(element, mangas, baseUrl);
        processedCount++;
      } catch (Exception e) {
        log.warning("Error al procesar elemento de manga: " + e.getMessage());
        errorCount++;
      }
    }

    return new ProcessingStats(processedCount, errorCount);
  }


  private void processSingleMangaElement(Element element, List<MangaModel> mangas, String baseUrl) {
    MangaModel manga = parseMangaElement(element);
    if (manga != null) {
      // Construir detalles adicionales (descripción, último capítulo)
      mangaDetailsBuilder.buildMangaDetails(manga, baseUrl);
      mangas.add(manga);
      log.fine("Manga agregado: " + manga.getName());
    }
  }

  
  private static class ProcessingStats {
    final int processedCount;
    final int errorCount;

    ProcessingStats(int processedCount, int errorCount) {
      this.processedCount = processedCount;
      this.errorCount = errorCount;
    }
  }
}






