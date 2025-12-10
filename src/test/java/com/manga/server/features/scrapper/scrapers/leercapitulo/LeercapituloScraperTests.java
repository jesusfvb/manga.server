package com.manga.server.features.scrapper.scrapers.leercapitulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas vacía cuando el HTML es inválido")
    void testGetMangasWithNewChaptersInvalid() throws IOException {
        // Given
        String html = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_with_new_chapters_bad.html"));

        Document documentDescription = Document.createShell("http://example.com");
        documentDescription.html(html);

        Document document = Document.createShell("http://example.com");
        document.html(html);
        when(jsoupWrapper.getDocument(anyString())).thenReturn(document);

        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();

        verify(jsoupWrapper, times(1)).getDocument(anyString());
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
        verify(jsoupWrapper, times(1)).getDocument(anyString());
        assertEquals(0, result.size());

    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe omitir mangas con URL vacía")
    void testGetMangasWithNewChaptersEmptyUrl() throws IOException {
        // Given
        String html = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_with_new_chapters_empty_url.html"));

        Document document = Document.createShell("http://example.com");
        document.html(html);

        String htmlDescription = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

        Document documentDescription = Document.createShell("http://example.com");
        documentDescription.html(htmlDescription);

        when(jsoupWrapper.getDocument(anyString())).thenReturn(document, documentDescription);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size(), "Debe retornar solo el manga con URL válida");
        assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
        assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());
        verify(jsoupWrapper, times(2)).getDocument(anyString());
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe omitir mangas con último capítulo inválido")
    void testGetMangasWithNewChaptersInvalidLastChapter() throws IOException {
        // Given
        String html = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_with_new_chapters_invalid_last_chapter.html"));

        Document document = Document.createShell("http://example.com");
        document.html(html);

        String htmlDescription = Files.readString(
                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

        Document documentDescription = Document.createShell("http://example.com");
        documentDescription.html(htmlDescription);

        when(jsoupWrapper.getDocument(anyString())).thenReturn(document, documentDescription);
        List<MangaModel> result = leercapituloScraper.getMangasWithNewChapters();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size(), "Debe retornar solo el manga con último capítulo válido");
        assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
        assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());
        assertEquals(38.0, result.get(0).getLastChapter(), "Debe tener el último capítulo correcto");
        verify(jsoupWrapper, times(2)).getDocument(anyString());
    }

}
