package com.manga.server.features.scrapper.scrapers.leercapitulo.helpers;

/**
 * Centraliza constantes de selectores CSS utilizados en LeerCapitulo.
 * Facilita mantenimiento y cambios de estructura HTML del sitio.
 */
public class LeerCapituloSelectors {

  // Selectores para getMangasWithNewChapters
  public static final String MANGA_CONTAINER = "div.media.mainpage-manga";
  public static final String MANGA_NAME = "div.media-body > a > h4.manga-newest";
  public static final String MANGA_URL = "div.media-body > a";
  public static final String MANGA_THUMBNAIL = "div.media-left.cover-manga > a > img.media-object.lozad";
  public static final String MANGA_LAST_CHAPTER = "div.media-body > div.row > div.col-xs-11 > div.hotup-list > span:first-child > a.xanh";

  // Selectores para getChapters
  public static final String CHAPTERS_LIST = "#examples > div > div > ul > li > div > h4 > a";

  // Selectores para getImg
  public static final String IMAGES_CONTAINER = ".comic_wraCon > a";

  // Selectores para buildManga (detalles)
  public static final String MANGA_DESCRIPTION = "#example2";
  public static final String LAST_CHAPTER_ELEMENT = "#examples > div > div > ul > li:nth-child(1) > div > h4 > a";

  // Atributos HTML
  public static final String THUMBNAIL_ATTRIBUTE = "data-src";
  public static final String IMAGE_ATTRIBUTE = "data-src";
  public static final String IMAGE_NUMBER_ATTRIBUTE = "name";
  public static final String URL_ATTRIBUTE = "href";

  // Scripts iniciales para Playwright
  public static final String DISPLAY_MODE_SCRIPT = "localStorage.setItem('display_mode', '1')";

  // Endpoints de API
  public static final String SEARCH_ENDPOINT = "/search-autocomplete?term=";

  // Patrones de parsing
  public static final String CHAPTER_PREFIX = "Capitulo";
  public static final String CHAPTER_SEPARATOR = ":";

  private LeerCapituloSelectors() {
    // Clase de constantes, no se debe instanciar
  }
}

