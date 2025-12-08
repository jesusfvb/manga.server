package com.manga.server.features.scrapper.scrapers.leercapitulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.core.browser.PlaywrightManager;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.shared.enums.ScrappersEnum;

/**
 * Tests para el método getMangasWithNewChapters de LeercapituloScraper.
 * Estos tests se enfocan en verificar el parsing del HTML que es la parte más
 * crítica del método.
 */
@ExtendWith(MockitoExtension.class)
public class LeercapituloScraperTests {

    @InjectMocks
    private LeercapituloScraper leercapituloScraper;

    @Mock
    private PlaywrightManager playwrightManager;

    @Mock
    private JsoupWrapper jsoupWrapper;

    private String createHtmlWithMangas(int count, int invalidUrlCount) {
        StringBuilder html = new StringBuilder(
                "<html><body>");

        for (int i = 1; i <= count; i++) {
            html.append(String.format(
                    "<div class=\"media mainpage-manga\">" +
                            "<div class=\"media-left cover-manga\">" +
                            "<a><img class=\"media-object lozad\" data-src=\"https://example.com/thumb%d.jpg\"></a>" +
                            "</div>" +
                            "<div class=\"media-body\">" +
                            "<a href=\"/manga%d\">" +
                            "<h4 class=\"manga-newest\">Manga %d</h4>" +
                            "</a>" +
                            "<div class=\"row\">" +
                            "<div class=\"col-xs-11\">" +
                            "<div class=\"hotup-list\">" +
                            "<span><a class=\"xanh\">Capitulo %d.0</a></span>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</div>",
                    i, i, i, i));
        }
        for (int i = 1; i <= invalidUrlCount; i++) {
            html.append(createHtmlWithInvalidUrl());
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String createHtmlWithInvalidManga() {
        return "<html><body>" +
                "<div class=\"media mainpage-manga\">" +
                "<div class=\"media-body\">" +
                "<a href=\"\">" + // URL vacía
                "<h4 class=\"manga-newest\"></h4>" + // Nombre vacío
                "</a>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String createHtmlWithInvalidChapterNumber() {
        return "<html><body>" +
                "<div class=\"media mainpage-manga\">" +
                "<div class=\"media-left cover-manga\">" +
                "<a><img class=\"media-object lozad\" data-src=\"https://example.com/thumb.jpg\"></a>" +
                "</div>" +
                "<div class=\"media-body\">" +
                "<a href=\"/manga1\">" +
                "<h4 class=\"manga-newest\">Manga Test</h4>" +
                "</a>" +
                "<div class=\"row\">" +
                "<div class=\"col-xs-11\">" +
                "<div class=\"hotup-list\">" +
                "<span><a class=\"xanh\">Capitulo inválido</a></span>" + // Número inválido
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String createHtmlWithInvalidUrl() {
        return "<html><body>" +
                "<div class=\"media mainpage-manga\">" +
                "<div class=\"media-body\">" +
                "<a href=\"\">" + // URL vacía
                "<h4 class=\"manga-newest\"></h4>" + // Nombre vacío
                "</a>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas cuando el HTML es válido")
    void testGetMangasWithNewChaptersSuccess() throws IOException {
        // Given
        String html = createHtmlWithMangas(3, 0);

        Document document = Document.createShell("http://example.com");
        document.html(html);

        when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas vacia cuando el HTML es inválido")
    void testGetMangasWithNewChaptersInvalid() throws IOException {
        // Given
        String html = createHtmlWithInvalidManga();
        Document document = Document.createShell("http://example.com");
        document.html(html);
        when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe manejar el error de JsoupWrapper.getDocument")
    void testGetMangasWithNewChaptersJsoupWrapperError() throws IOException {
        // Given
        when(jsoupWrapper.getDocument(anyString())).thenThrow(new IOException("Error de conexión"));
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
        assertNotNull(result);
        assertEquals(0, result.size());

    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe manejar el error de NumberFormatException")
    void testGetMangasWithNewChaptersNumberFormatException() throws IOException {
        // Given
        String html = createHtmlWithInvalidChapterNumber();
        Document document = Document.createShell("http://example.com");
        document.html(html);
        when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe manejar la url vacia")
    void testGetMangasWithNewChaptersEmptyUrl() throws IOException {
        // Given
        String html = createHtmlWithMangas(1, 1);
        Document document = Document.createShell("http://example.com");
        document.html(html);
        when(jsoupWrapper.getDocument(anyString())).thenReturn(document);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
