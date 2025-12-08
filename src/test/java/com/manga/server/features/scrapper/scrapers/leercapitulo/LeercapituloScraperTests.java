package com.manga.server.features.scrapper.scrapers.leercapitulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.core.browser.PlaywrightManager;
import com.manga.server.features.manga.model.MangaModel;

/**
 * Tests para el método getMangasWithNewChapters de LeercapituloScraper.
 * Estos tests se enfocan en verificar el parsing del HTML que es la parte más
 * crítica del método.
 */
@ExtendWith(MockitoExtension.class)
public class LeercapituloScraperTests {

    @Mock
    private PlaywrightManager playwrightManager;

    @Mock
    private JsoupWrapper jsoupWrapper;

    @InjectMocks
    private LeercapituloScraper leercapituloScraper;

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas cuando el HTML es válido")
    void testGetMangasWithNewChaptersSuccess() throws IOException {
        // Given
        String html = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_with_new_chapters.html"));

        Document document = Document.createShell("http://example.com");
        document.html(html);

        String htmlDescription = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

        Document documentDescription = Document.createShell("http://example.com");
        documentDescription.html(htmlDescription);

        when(jsoupWrapper.getDocument(anyString())).thenReturn(document, documentDescription);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();

        assertNotNull(result);
        assertEquals(result.get(0).getName(), "Hikaru no shinda Natsu");
        assertEquals(result.get(0).getUrl().getUrl(), "/manga/vu8qr59pze/hikaru-no-shinda-natsu/");
        assertEquals(result.get(0).getThumbnail().getUrl(),
                "/covers/9c/d32271545195bef29c236bf03b7c5a.jpg?v1762641074242");
        assertEquals(result.get(0).getDescription(), "Manga Description");
        assertEquals(result.get(0).getLastChapter(), 38.0);
        assertEquals(5, result.size());
    }

    // @Test
    // @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas vacia
    // cuando el HTML es inválido")
    // void testGetMangasWithNewChaptersInvalid() throws IOException {
    // // Given
    // String html = createHtmlWithInvalidManga();
    // Document document = Document.createShell("http://example.com");
    // document.html(html);
    // when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
    // List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
    // assertNotNull(result);
    // assertEquals(0, result.size());
    // }

    // @Test
    // @DisplayName("getMangasWithNewChapters - Debe manejar el error de
    // JsoupWrapper.getDocument")
    // void testGetMangasWithNewChaptersJsoupWrapperError() throws IOException {
    // // Given
    // when(jsoupWrapper.getDocument(anyString())).thenThrow(new IOException("Error
    // de conexión"));
    // List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
    // assertNotNull(result);
    // assertEquals(0, result.size());

    // }

    // @Test
    // @DisplayName("getMangasWithNewChapters - Debe manejar el error de
    // NumberFormatException")
    // void testGetMangasWithNewChaptersNumberFormatException() throws IOException {
    // // Given
    // String html = createHtmlWithInvalidChapterNumber();
    // Document document = Document.createShell("http://example.com");
    // document.html(html);
    // when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
    // List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
    // assertNotNull(result);
    // assertEquals(0, result.size());
    // }

    // @Test
    // @DisplayName("getMangasWithNewChapters - Debe manejar la url vacia")
    // void testGetMangasWithNewChaptersEmptyUrl() throws IOException {
    // // Given
    // String html = createHtmlWithMangas(1, 1);
    // Document document = Document.createShell("http://example.com");
    // document.html(html);
    // when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
    // List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
    // assertNotNull(result);
    // assertEquals(1, result.size());
    // }

    // // Tests para searchMangas

    // @Test
    // @DisplayName("searchMangas - Debe retornar lista vacía cuando query es null")
    // void testSearchMangasNullQuery() {
    // // When
    // List<MangaModel> result = leercapituloScraper.searchMangas(null);

    // // Then
    // assertNotNull(result);
    // assertEquals(0, result.size());
    // }

    // @Test
    // @DisplayName("searchMangas - Debe retornar lista vacía cuando query está
    // vacío")
    // void testSearchMangasEmptyQuery() {
    // // When
    // List<MangaModel> result = leercapituloScraper.searchMangas("");

    // // Then
    // assertNotNull(result);
    // assertEquals(0, result.size());
    // }

    // @Test
    // @DisplayName("searchMangas - Debe retornar lista vacía cuando query solo
    // tiene espacios")
    // void testSearchMangasBlankQuery() {
    // // When
    // List<MangaModel> result = leercapituloScraper.searchMangas(" ");

    // // Then - El método no verifica espacios en blanco, así que intentará hacer
    // la
    // // búsqueda
    // // pero si falla, retornará lista vacía
    // assertNotNull(result);
    // }

    // @Test
    // @DisplayName("searchMangas - Debe retornar lista cuando se ejecuta con query
    // válida (test de integración básico)")
    // void testSearchMangasWithValidQuery() {
    // // Given - Este test verifica que el método no lanza excepciones
    // // y retorna una lista (puede estar vacía si hay error de conexión)
    // String query = "One Piece";

    // // When
    // List<MangaModel> result = leercapituloScraper.searchMangas(query);

    // // Then
    // assertNotNull(result);
    // // No verificamos el tamaño porque depende de la conexión a internet
    // }

    // @Test
    // @DisplayName("searchMangas - Debe manejar errores de conexión correctamente")
    // void testSearchMangasConnectionError() {
    // // Given - El método maneja excepciones internamente
    // String query = "test query";

    // // When - Si hay error de conexión, el método debe retornar lista vacía
    // List<MangaModel> result = leercapituloScraper.searchMangas(query);

    // // Then
    // assertNotNull(result);
    // // El método captura excepciones y retorna lista vacía o lista con resultados
    // // parciales
    // }

    // @Test
    // @DisplayName("searchMangas - Debe ejecutarse sin errores con query válida")
    // void testSearchMangasExecutesWithoutErrors() {
    // // Given
    // String query = "naruto";

    // // When - Verificamos que el método se ejecuta sin errores
    // // La URI construida sería: baseUrl + "/search-autocomplete?term=" + query
    // List<MangaModel> result = leercapituloScraper.searchMangas(query);

    // // Then
    // assertNotNull(result);
    // // No podemos verificar la URI directamente sin mockear RestClient,
    // // pero el test verifica que el método no falla y retorna una lista
    // }
}
