package com.manga.server.features.scrapper.scrapers.leercapitulo;

import java.io.IOException;
import java.time.LocalDateTime;
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
    long startTime = System.currentTimeMillis();
    log.info("Iniciando getMangasWithNewChapters - Thread: " + Thread.currentThread().getName());
    
    List<MangaModel> mangas = new LinkedList<>();
    String baseUrl = baseURl();
    
    try {
      log.info("Conectando a la URL base: " + baseUrl);
      Document document = Jsoup.connect(baseUrl).get();
      log.info("Conexión exitosa a " + baseUrl);

      String selector = "body > section > div > div > div.col-md-8 > div > div > div";
      log.fine("Buscando elementos con selector: " + selector);
      Elements elements = document.select(selector);
      log.info("Elementos encontrados: " + elements.size());

      int processedCount = 0;
      int errorCount = 0;
      
      for (var element : elements) {
        try {
          String name = element.select("div.media-body > a > h4").text();
          String url = element.select("div.media-body > a").attr("href");
          String thumbnail = element.select("div > div.media-left.cover-manga > a > img").attr("data-src");
          String lastChapterText = element.select("div.media-body > div > div > div > span:nth-child(1) > a")
              .text().replace("Capitulo", "").trim();

          log.fine("Procesando manga - Nombre: " + name + ", URL: " + url + ", Último capítulo: " + lastChapterText);

          if (name == null || name.isEmpty()) {
            log.warning("Manga sin nombre encontrado, omitiendo");
            errorCount++;
            continue;
          }

          if (url == null || url.isEmpty()) {
            log.warning("Manga '" + name + "' sin URL, omitiendo");
            errorCount++;
            continue;
          }

          try {
            double lastChapter = Double.parseDouble(lastChapterText);
            MangaModel manga = MangaModel.builder()
                .name(name)
                .url(baseUrl + url)
                .thumbnail(baseUrl + thumbnail)
                .lastChapter(lastChapter)
                .build();
            
            mangas.add(manga);
            log.fine("Manga agregado: " + name + " (Capítulo " + lastChapter + ")");
            
            // Construir detalles del manga
            log.fine("Construyendo detalles para manga: " + name);
            buildManga(manga);
            processedCount++;
            
          } catch (NumberFormatException e) {
            log.warning("Error al parsear último capítulo '" + lastChapterText + "' para manga '" + name + "': " + e.getMessage());
            errorCount++;
          }

        } catch (Exception e) {
          log.warning("Error al procesar elemento de manga: " + e.getMessage());
          errorCount++;
        }
      }

      long duration = System.currentTimeMillis() - startTime;
      log.info(String.format("getMangasWithNewChapters completado - Mangas procesados: %d, Errores: %d, Total: %d, Duración: %d ms", 
          processedCount, errorCount, mangas.size(), duration));

    } catch (IOException e) {
      long duration = System.currentTimeMillis() - startTime;
      log.severe("Error de IO en getMangasWithNewChapters después de " + duration + " ms: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      log.severe("Error inesperado en getMangasWithNewChapters después de " + duration + " ms: " + e.getMessage());
      e.printStackTrace();
    }
    
    log.info("Finalizando getMangasWithNewChapters - Retornando " + mangas.size() + " mangas");
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
            .lastUpdated(LocalDateTime.now())
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
    long startTime = System.currentTimeMillis();
    log.info("Iniciando getImg - URL: " + url + ", Thread: " + Thread.currentThread().getName());
    
    final String finalUrl = url.contains(baseURl()) ? url : baseURl() + url;
    List<ImgModel> imgModels = new LinkedList<>();
    Page page = null;
    
    try {
      log.fine("Obteniendo contexto de Playwright para URL: " + finalUrl);
      var context = playwrightManager.getContext();
      
      if (context == null) {
        log.severe("El contexto de Playwright es null para URL: " + finalUrl);
        return imgModels;
      }
      
      log.fine("Contexto de Playwright obtenido. Agregando script de inicialización");
      try {
        context.addInitScript("localStorage.setItem('display_mode', '1')");
        log.fine("Script de inicialización agregado exitosamente");
      } catch (Exception e) {
        log.warning("Error al agregar script de inicialización (contexto puede estar cerrado): " + e.getMessage());
        throw e;
      }
      
      log.fine("Creando nueva página para URL: " + finalUrl);
      page = context.newPage();
      log.info("Página creada exitosamente para URL: " + finalUrl);
      
      log.info("Navegando a URL: " + finalUrl);
      page.navigate(finalUrl);
      log.fine("Esperando a que la página cargue completamente");
      page.waitForLoadState();
      log.info("Página cargada exitosamente");
      
      log.fine("Buscando imágenes con selector: .comic_wraCon > a");
      var images = page.querySelectorAll(".comic_wraCon > a");
      log.info("Imágenes encontradas: " + images.size());
      
      int processedCount = 0;
      int errorCount = 0;
      
      for (var image : images) {
        try {
          String img = image.querySelector("img").getAttribute("data-src");
          String number = image.getAttribute("name");
          
          if (img != null && !img.isEmpty() && number != null && !number.isEmpty()) {
            try {
              int imgNumber = Integer.parseInt(number);
              imgModels.add(ImgModel.builder()
                  .number(imgNumber)
                  .url(img)
                  .lastUpdated(LocalDateTime.now())
                  .build());
              processedCount++;
              log.fine("Imagen agregada - Número: " + imgNumber + ", URL: " + img);
            } catch (NumberFormatException e) {
              log.warning("Error al parsear número de imagen '" + number + "': " + e.getMessage());
              errorCount++;
            }
          } else {
            log.fine("Imagen omitida - img: " + (img != null ? "presente" : "null") + ", number: " + (number != null ? "presente" : "null"));
          }
        } catch (Exception e) {
          log.warning("Error al procesar imagen: " + e.getMessage());
          errorCount++;
        }
      }

      long duration = System.currentTimeMillis() - startTime;
      log.info(String.format("getImg completado - Imágenes procesadas: %d, Errores: %d, Total: %d, Duración: %d ms", 
          processedCount, errorCount, imgModels.size(), duration));

    } catch (IllegalStateException e) {
      long duration = System.currentTimeMillis() - startTime;
      if (e.getMessage() != null && e.getMessage().contains("state should be: open")) {
        log.severe(String.format("ERROR: Contexto de Playwright cerrado o en estado inválido después de %d ms - URL: %s, Thread: %s, Mensaje: %s", 
            duration, finalUrl, Thread.currentThread().getName(), e.getMessage()));
        e.printStackTrace();
      } else {
        log.severe("ERROR: Estado ilegal en getImg después de " + duration + " ms - URL: " + finalUrl + ", Mensaje: " + e.getMessage());
        e.printStackTrace();
      }
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      log.severe(String.format("Error inesperado en getImg después de %d ms - URL: %s, Thread: %s, Tipo: %s, Mensaje: %s", 
          duration, finalUrl, Thread.currentThread().getName(), e.getClass().getSimpleName(), e.getMessage()));
      e.printStackTrace();
    } finally {
      if (page != null) {
        try {
          log.fine("Cerrando página para URL: " + finalUrl);
          page.close();
          log.fine("Página cerrada exitosamente");
        } catch (Exception e) {
          log.warning("Error al cerrar página: " + e.getMessage());
        }
      }
    }

    if (imgModels.size() >= 1) {
      log.fine("Ordenando " + imgModels.size() + " imágenes por número");
      imgModels.sort(Comparator.comparingInt(ImgModel::getNumber));
    }
    
    log.info("Finalizando getImg - Retornando " + imgModels.size() + " imágenes para URL: " + finalUrl);
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
