package com.manga.server.features.scrapper.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.registry.ScraperRegistry;
import com.manga.server.features.scrapper.scrapers.Scraper;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class ScrapperServiceTests {

    @Mock
    private ScraperRegistry scraperRegistry;

    @Mock
    private Scraper scraper;

    @InjectMocks
    private ScrapperService scrapperService;

    private MangaModel mangaModel1;
    private MangaModel mangaModel2;
    private List<MangaModel> mangaModels;

    @BeforeEach
    void setUp() {
        mangaModel1 = MangaModel.builder()
                .id("1")
                .name("One Piece")
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .lastChapter(1100.0)
                .build();

        mangaModel2 = MangaModel.builder()
                .id("2")
                .name("Naruto")
                .url(UrlModel.builder()
                        .url("https://example.com/naruto")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .lastChapter(700.0)
                .build();

        mangaModels = Arrays.asList(mangaModel1, mangaModel2);
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista de mangas cuando el scraper retorna mangas")
    void testGetMangasWithNewChaptersSuccess() {
        // Given
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getMangasWithNewChapters()).thenReturn(mangaModels);

        // When
        List<MangaModel> result = scrapperService.getMangasWithNewChapters();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("One Piece", result.get(0).getName());
        assertEquals("Naruto", result.get(1).getName());
        assertEquals(1100.0, result.get(0).getLastChapter());
        assertEquals(700.0, result.get(1).getLastChapter());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getMangasWithNewChapters();
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar null cuando el scraper retorna null")
    void testGetMangasWithNewChaptersNull() {
        // Given
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getMangasWithNewChapters()).thenReturn(null);

        // When
        List<MangaModel> result = scrapperService.getMangasWithNewChapters();

        // Then
        assertNull(result);
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getMangasWithNewChapters();
    }

    @Test
    @DisplayName("getMangasWithNewChapters - Debe retornar lista vacía cuando el scraper retorna lista vacía")
    void testGetMangasWithNewChaptersEmptyList() {
        // Given
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getMangasWithNewChapters()).thenReturn(Collections.emptyList());

        // When
        List<MangaModel> result = scrapperService.getMangasWithNewChapters();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getMangasWithNewChapters();
    }

    // Tests para searchManga

    @Test
    @DisplayName("searchManga - Debe retornar lista de mangas cuando el scraper retorna mangas con leerCapitulo")
    void testSearchMangaSuccess() {
        // Given
        String query = "One Piece";
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(mangaModels);

        // When
        List<MangaModel> result = scrapperService.searchManga( query);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("One Piece", result.get(0).getName());
        assertEquals("Naruto", result.get(1).getName());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe retornar null cuando el scraper retorna null")
    void testSearchMangaNull() {
        // Given
        String query = "Bleach";
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(null);

        // When
        List<MangaModel> result = scrapperService.searchManga(query);

        // Then
        assertNull(result);
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe retornar lista vacía cuando el scraper retorna lista vacía")
    void testSearchMangaEmptyList() {
        // Given
        String query = "Dragon Ball";
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(Collections.emptyList());

        // When
        List<MangaModel> result = scrapperService.searchManga( query);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe retornar lista con un solo manga cuando el scraper retorna un manga")
    void testSearchMangaSingleManga() {
        // Given
        String query = "One Piece";
        List<MangaModel> singleManga = Arrays.asList(mangaModel1);
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(singleManga);

        // When
        List<MangaModel> result = scrapperService.searchManga( query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("One Piece", result.get(0).getName());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe pasar la query correctamente al scraper")
    void testSearchMangaQueryPassedCorrectly() {
        // Given
        String query = "Naruto Shippuden";
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(mangaModels);

        // When
        scrapperService.searchManga( query);

        // Then
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe manejar query vacía")
    void testSearchMangaEmptyQuery() {
        // Given
        String query = "";
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(Collections.emptyList());

        // When
        List<MangaModel> result = scrapperService.searchManga( query);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    @Test
    @DisplayName("searchManga - Debe manejar query null")
    void testSearchMangaNullQuery() {
        // Given
        String query = null;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.searchMangas(query)).thenReturn(Collections.emptyList());

        // When
        List<MangaModel> result = scrapperService.searchManga( query);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).searchMangas(query);
    }

    // Tests para getChapters

    @Test
    @DisplayName("getChapters - Debe retornar lista de capítulos cuando el scraper retorna capítulos con leerCapitulo")
    void testGetChaptersSuccess() {
        // Given
        String mangaUrl = "/one-piece";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();

        ChapterModel chapter1 = ChapterModel.builder()
                .id("1")
                .number(1100.0)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1100")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .mangaId("manga-1")
                .build();

        ChapterModel chapter2 = ChapterModel.builder()
                .id("2")
                .number(1101.0)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1101")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .mangaId("manga-1")
                .build();

        List<ChapterModel> chapters = Arrays.asList(chapter1, chapter2);
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(chapters);

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1100.0, result.get(0).getNumber());
        assertEquals(1101.0, result.get(1).getNumber());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    @Test
    @DisplayName("getChapters - Debe retornar null cuando el scraper retorna null")
    void testGetChaptersNull() {
        // Given
        String mangaUrl = "/naruto";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(null);

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNull(result);
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    @Test
    @DisplayName("getChapters - Debe retornar lista vacía cuando el scraper retorna lista vacía")
    void testGetChaptersEmptyList() {
        // Given
        String mangaUrl = "/bleach";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(Collections.emptyList());

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    @Test
    @DisplayName("getChapters - Debe retornar lista con un solo capítulo cuando el scraper retorna un capítulo")
    void testGetChaptersSingleChapter() {
        // Given
        String mangaUrl = "/one-piece";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();

        ChapterModel chapter = ChapterModel.builder()
                .id("1")
                .number(1100.0)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1100")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .mangaId("manga-1")
                .build();

        List<ChapterModel> singleChapter = Arrays.asList(chapter);
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(singleChapter);

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1100.0, result.get(0).getNumber());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    @Test
    @DisplayName("getChapters - Debe pasar la URL correctamente al scraper")
    void testGetChaptersUrlPassedCorrectly() {
        // Given
        String mangaUrl = "/dragon-ball";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(Collections.emptyList());

        // When
        scrapperService.getChapters(urlModel);

        // Then
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
        verify(scraper).getChapters(argThat(url -> url.equals(mangaUrl)));
    }

    @Test
    @DisplayName("getChapters - Debe manejar URL vacía")
    void testGetChaptersEmptyUrl() {
        // Given
        String mangaUrl = "";
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(Collections.emptyList());

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    @Test
    @DisplayName("getChapters - Debe manejar URL null")
    void testGetChaptersNullUrl() {
        // Given
        String mangaUrl = null;
        UrlModel urlModel = UrlModel.builder()
                .url(mangaUrl)
                .scrapper(ScrappersEnum.leerCapitulo)
                .build();
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getChapters(mangaUrl)).thenReturn(Collections.emptyList());

        // When
        List<ChapterModel> result = scrapperService.getChapters(urlModel);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getChapters(mangaUrl);
    }

    // Tests para getImg

    @Test
    @DisplayName("getImg - Debe retornar lista de imágenes cuando el scraper retorna imágenes con leerCapitulo")
    void testGetImgSuccess() {
        // Given
        String chapterUrl = "/one-piece/chapter-1100";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;

        ImgModel img1 = ImgModel.builder()
                .id("1")
                .number(1)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1100/page-1.jpg")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .scrapper(ScrappersEnum.leerCapitulo)
                .chapterId("chapter-1")
                .build();

        ImgModel img2 = ImgModel.builder()
                .id("2")
                .number(2)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1100/page-2.jpg")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .scrapper(ScrappersEnum.leerCapitulo)
                .chapterId("chapter-1")
                .build();

        List<ImgModel> images = Arrays.asList(img1, img2);
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(images);

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getNumber());
        assertEquals(2, result.get(1).getNumber());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

    @Test
    @DisplayName("getImg - Debe retornar null cuando el scraper retorna null")
    void testGetImgNull() {
        // Given
        String chapterUrl = "/naruto/chapter-700";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(null);

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNull(result);
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

    @Test
    @DisplayName("getImg - Debe retornar lista vacía cuando el scraper retorna lista vacía")
    void testGetImgEmptyList() {
        // Given
        String chapterUrl = "/bleach/chapter-686";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(Collections.emptyList());

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

    @Test
    @DisplayName("getImg - Debe retornar lista con una sola imagen cuando el scraper retorna una imagen")
    void testGetImgSingleImage() {
        // Given
        String chapterUrl = "/one-piece/chapter-1100";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;

        ImgModel img = ImgModel.builder()
                .id("1")
                .number(1)
                .url(UrlModel.builder()
                        .url("/one-piece/chapter-1100/page-1.jpg")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .scrapper(ScrappersEnum.leerCapitulo)
                .chapterId("chapter-1")
                .build();

        List<ImgModel> singleImage = Arrays.asList(img);
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(singleImage);

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumber());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

    @Test
    @DisplayName("getImg - Debe pasar la URL correctamente al scraper")
    void testGetImgUrlPassedCorrectly() {
        // Given
        String chapterUrl = "/dragon-ball/chapter-1";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(Collections.emptyList());

        // When
        scrapperService.getImg(scrapper, chapterUrl);

        // Then
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
        verify(scraper).getImg(argThat(url -> url.equals(chapterUrl)));
    }

    @Test
    @DisplayName("getImg - Debe manejar URL vacía")
    void testGetImgEmptyUrl() {
        // Given
        String chapterUrl = "";
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(Collections.emptyList());

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

    @Test
    @DisplayName("getImg - Debe manejar URL null")
    void testGetImgNullUrl() {
        // Given
        String chapterUrl = null;
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(scraperRegistry.getScraper(ScrappersEnum.leerCapitulo)).thenReturn(scraper);
        when(scraper.getImg(chapterUrl)).thenReturn(Collections.emptyList());

        // When
        List<ImgModel> result = scrapperService.getImg(scrapper, chapterUrl);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(scraperRegistry, times(1)).getScraper(ScrappersEnum.leerCapitulo);
        verify(scraper, times(1)).getImg(chapterUrl);
    }

}
