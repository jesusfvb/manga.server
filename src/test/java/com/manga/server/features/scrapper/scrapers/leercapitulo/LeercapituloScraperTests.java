package com.manga.server.features.scrapper.scrapers.leercapitulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manga.server.core.browser.JsoupWrapper;
import com.manga.server.core.browser.PlaywrightManager;
import com.manga.server.core.browser.RestClientWrapper;
import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.scrapers.leercapitulo.dtos.LeerCapituloSearchDTO;

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

        @Mock
        private RestClientWrapper restClientWrapper;

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

        @Test
        @DisplayName("searchMangas - Debe retornar lista de mangas cuando la búsqueda es exitosa")
        @SuppressWarnings("unchecked")
        void testSearchMangasSuccess() throws Exception {
                // Given
                String query = "hikaru";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                assertEquals(3, result.size(), "Debe retornar 3 mangas");
                assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
                assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());
                assertEquals("/covers/9c/d32271545195bef29c236bf03b7c5a.jpg?v1762641074242",
                                result.get(0).getThumbnail().getUrl());
                assertEquals("Manga Description", result.get(0).getDescription());
                assertEquals(136.0, result.get(0).getLastChapter(), "Debe tener el último capítulo correcto");

                assertEquals("Bai Lijin entre los mortales", result.get(1).getName());
                assertEquals("Nakamura-san, la Gyaru no invitada", result.get(2).getName());

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));
                verify(jsoupWrapper, times(3)).getDocument(anyString());
        }

        @Test
        @DisplayName("searchMangas - Debe procesar mangas con URL vacía")
        @SuppressWarnings("unchecked")
        void testSearchMangasWithEmptyUrl() throws Exception {
                // Given
                String query = "test";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response_empty_url.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                assertEquals(1, result.size(), "Debe retornar 1 manga");
                assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
                assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));
                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("searchMangas - Debe procesar mangas con nombre vacío")
        @SuppressWarnings("unchecked")
        void testSearchMangasWithEmptyName() throws Exception {
                // Given
                String query = "test";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response_empty_name.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                assertEquals(1, result.size(), "Debe retornar 1 manga");
                assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
                assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));
                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("searchMangas - Debe procesar mangas con thumbnail vacío")
        @SuppressWarnings("unchecked")
        void testSearchMangasWithEmptyThumbnail() throws Exception {
                // Given
                String query = "test";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response_empty_thumbnail.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                assertEquals(1, result.size(), "Debe retornar 1 manga");
                assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
                assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());
                assertEquals("/covers/9c/d32271545195bef29c236bf03b7c5a.jpg?v1762641074242",
                                result.get(0).getThumbnail().getUrl());

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));
                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("searchMangas - Debe omitir mangas completamente vacíos")
        @SuppressWarnings("unchecked")
        void testSearchMangasWithCompletelyEmptyManga() throws Exception {
                // Given
                String query = "test";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response_completely_empty.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                assertEquals(1, result.size(), "Debe retornar solo el manga válido");
                assertEquals("Hikaru no shinda Natsu", result.get(0).getName());
                assertEquals("/manga/vu8qr59pze/hikaru-no-shinda-natsu/", result.get(0).getUrl().getUrl());
                assertEquals("/covers/9c/d32271545195bef29c236bf03b7c5a.jpg?v1762641074242",
                                result.get(0).getThumbnail().getUrl());

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));
                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("searchMangas - Debe manejar correctamente 50 ejemplos con todos los casos posibles")
        @SuppressWarnings("unchecked")
        void testSearchMangasComprehensive() throws Exception {
                // Given
                String query = "test";
                String jsonResponse = Files.readString(
                                Paths.get("src/test/resources/json/leercapitulo/search_response_comprehensive.json"));

                ObjectMapper objectMapper = new ObjectMapper();
                List<LeerCapituloSearchDTO> searchResults = objectMapper.readValue(jsonResponse,
                                new TypeReference<List<LeerCapituloSearchDTO>>() {
                                });

                String htmlDescription = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document documentDescription = Document.createShell("http://example.com");
                documentDescription.html(htmlDescription);

                when(restClientWrapper.get(anyString(), any(TypeReference.class))).thenReturn(searchResults);
                // Retornar el documento siempre (una por cada manga en buildManga)
                when(jsoupWrapper.getDocument(anyString())).thenReturn(documentDescription);

                // When
                List<MangaModel> result = leercapituloScraper.searchMangas(query);

                // Then
                assertNotNull(result);
                // Debe procesar todos los mangas válidos (25 primeros + 7 últimos = 32 mangas
                // válidos)
                // Los mangas con campos vacíos también se procesan según el código actual
                assertEquals(32, result.size(), "Debe procesar todos los mangas correctos");

                verify(restClientWrapper, times(1)).get(anyString(), any(TypeReference.class));

                verify(jsoupWrapper, times(32)).getDocument(anyString());
        }

        @Test
        @DisplayName("getChapters - Debe retornar lista de capítulos cuando el HTML es válido")
        void testGetChaptersSuccess() throws IOException {
                // Given
                String url = "/manga/2gw70ci10x/sss-class-gacha-hunter/";
                String html = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description.htm"));

                Document document = Document.createShell("http://example.com");
                document.html(html);

                when(jsoupWrapper.getDocument(anyString())).thenReturn(document);

                // When
                List<ChapterModel> result = leercapituloScraper.getChapters(url);

                // Then
                assertNotNull(result);
                assertEquals(10, result.size(), "Debe retornar 10 capítulos");

                // Verificar el primer capítulo (136)
                assertEquals(136.0, result.get(0).getNumber());
                assertEquals("/leer/2gw70ci10x/sss-class-gacha-hunter/136/", result.get(0).getUrl().getUrl());
                assertNotNull(result.get(0).getMangaId());
                assertNotNull(result.get(0).getLastUpdated());

                // Verificar el último capítulo (127)
                assertEquals(127.0, result.get(9).getNumber());
                assertEquals("/leer/2gw70ci10x/sss-class-gacha-hunter/127/", result.get(9).getUrl().getUrl());

                // Verificar algunos capítulos intermedios
                assertEquals(135.0, result.get(1).getNumber());
                assertEquals(134.0, result.get(2).getNumber());
                assertEquals(133.0, result.get(3).getNumber());

                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("getChapters - Debe retornar lista vacía cuando no hay capítulos")
        void testGetChaptersEmpty() throws IOException {
                // Given
                String url = "/manga/2gw70ci10x/sss-class-gacha-hunter/";
                String html = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description_no_chapters.htm"));

                Document document = Document.createShell("http://example.com");
                document.html(html);

                when(jsoupWrapper.getDocument(anyString())).thenReturn(document);

                // When
                List<ChapterModel> result = leercapituloScraper.getChapters(url);

                // Then
                assertNotNull(result);
                assertEquals(0, result.size(), "Debe retornar lista vacía cuando no hay capítulos");

                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("getChapters - Debe manejar correctamente el error de JsoupWrapper.getDocument")
        void testGetChaptersJsoupWrapperError() throws IOException {
                // Given
                String url = "/manga/2gw70ci10x/sss-class-gacha-hunter/";

                when(jsoupWrapper.getDocument(anyString())).thenThrow(new IOException("Error de conexión"));

                // When
                List<ChapterModel> result = leercapituloScraper.getChapters(url);

                // Then
                // El método retorna null cuando hay un error de IO
                assertEquals(null, result, "Debe retornar null cuando hay un error de IO");

                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

        @Test
        @DisplayName("getChapters - Debe retornar lista vacía cuando el HTML tiene estructura incorrecta")
        void testGetChaptersInvalidHtml() throws IOException {
                // Given
                String url = "/manga/2gw70ci10x/sss-class-gacha-hunter/";
                String html = Files.readString(
                                Paths.get("src/test/resources/html/leercapitulo/manga_description_invalid.htm"));

                Document document = Document.createShell("http://example.com");
                document.html(html);

                when(jsoupWrapper.getDocument(anyString())).thenReturn(document);

                // When
                List<ChapterModel> result = leercapituloScraper.getChapters(url);

                // Then
                assertNotNull(result);
                // Cuando el HTML no tiene la estructura correcta, el selector no encuentra elementos
                // y retorna una lista vacía
                assertEquals(0, result.size(), "Debe retornar lista vacía cuando el HTML tiene estructura incorrecta");

                verify(jsoupWrapper, times(1)).getDocument(anyString());
        }

}
