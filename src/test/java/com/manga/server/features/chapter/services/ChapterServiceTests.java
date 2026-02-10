package com.manga.server.features.chapter.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.manga.server.features.images.user_cases.ImgService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class ChapterServiceTests {

        @InjectMocks
        private ChapterService chapterService;

        @Mock
        private ScrapperService scrapperService;

        @Mock
        private ImgService imgService;

        @Mock
        private ChapterRepository chapterRepository;

        // Test getChapters

        @Test
        @DisplayName("getChapters - Debe de retornar array vació cuando no se proporciona un mangaId")
        void testGetChaptersMangaIdNull() {
                var chapters = chapterService.getChapters(null);
                assertEquals(0, chapters.size());
        }

        @Test
        @DisplayName("getChapters - Debe de retornar array vació cuando no allá capítulos en la base de datos y no se encuentre el manga en la base de datos")
        void testGetChaptersNoChaptersInDatabaseAndNoMangaInDatabase() {

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(List.of());
//                when(mangaService.getMangaById(any(String.class))).thenReturn(null);

                var chapters = chapterService.getChapters("1");
                assertEquals(0, chapters.size());
        }

        @Test
        @DisplayName("getChapters - Debe de retornar array vació cuando no allá capítulos en la base de datos y no allá encontrado nada en el scrapper")
        void testGetChaptersNoChaptersInDatabaseAndNoChaptersInScrapper() {
                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(List.of());
//                when(mangaService.getMangaById(any(String.class)))
//                                .thenReturn(MangaModel.builder().id("1").name("One Piece").url(UrlModel.builder()
//                                                .url("https://example.com/one-piece")
//                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build());
                when(scrapperService.getChapters(any(UrlModel.class))).thenReturn(List.of());
                var chapters = chapterService.getChapters("1");
                assertEquals(0, chapters.size());
        }

        @Test
        @DisplayName("getChapters - Debe de retornar array con los capítulos cuando se encuentran en la base de datos")
        void testGetChaptersFindChaptersInDatabase() {

                List<ChapterModel> chapters = new ArrayList<>(List.of(
                                ChapterModel.builder().id("1").number(1.0).url(UrlModel.builder()
                                                .url("https://example.com/one-piece")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(2.0).url(UrlModel.builder()
                                                .url("https://example.com/one-piece-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(chapters);

                var chaptersResult = chapterService.getChapters("123");

                assertEquals(chapters.size(), chaptersResult.size());
                assertEquals(chapters.get(0).getId(), chaptersResult.get(0).getId());
                assertEquals(chapters.get(0).getUrl(), chaptersResult.get(0).getUrl());
        }

        @SuppressWarnings({ "null", "unchecked" })
        @Test
        @DisplayName("getChapters - Debe de retornar array con los capítulos cuando no se encuentran en la base de datos y se encuentran en el scrapper")
        void testGetChaptersNoChaptersInDatabaseAndFindChaptersInScrapper() {

                List<ChapterModel> chapters = new LinkedList<>(List.of(
                                ChapterModel.builder().id("1").number(1.0).url(UrlModel.builder()
                                                .url("https://example.com/one-piece")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(2.0).url(UrlModel.builder()
                                                .url("https://example.com/one-piece-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(List.of());

//                when(mangaService.getMangaById(any(String.class)))
//                                .thenReturn(MangaModel.builder().id("1").name("One Piece").url(UrlModel.builder()
//                                                .url("https://example.com/one-piece")
//                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build());

                when(scrapperService.getChapters(any(UrlModel.class))).thenReturn(chapters);
                when(chapterRepository.saveAll(any(List.class))).thenReturn(chapters);

                var chaptersResult = chapterService.getChapters("1");

                assertEquals(chapters.size(), chaptersResult.size());
                assertEquals(chapters.get(0).getId(), chaptersResult.get(0).getId());
                assertEquals(chapters.get(0).getUrl(), chaptersResult.get(0).getUrl());
                assertEquals(chapters.get(1).getId(), chaptersResult.get(1).getId());
                assertEquals(chapters.get(1).getUrl(), chaptersResult.get(1).getUrl());
        }

        // Test getChapterById

        @Test
        @DisplayName("getChapterById - Debe retornar null cuando chapterId es null")
        void testGetChapterByIdWithNullId() {
                var result = chapterService.getChapterById(null);
                assertNull(result);
        }

        @Test
        @DisplayName("getChapterById - Debe retornar null cuando chapterId está vacío")
        void testGetChapterByIdWithEmptyId() {
                var result = chapterService.getChapterById("");
                assertNull(result);
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("getChapterById - Debe retornar null cuando el capítulo no existe en la base de datos")
        void testGetChapterByIdNotFound() {
                when(chapterRepository.findById(any(String.class))).thenReturn(Optional.empty());

                var result = chapterService.getChapterById("non-existent-id");
                assertNull(result);
        }

        @Test
        @DisplayName("getChapterById - Debe retornar el capítulo cuando existe en la base de datos")
        void testGetChapterByIdFound() {
                var chapterId = "chapter-123";
                var chapter = ChapterModel.builder()
                                .id(chapterId)
                                .number(1.0)
                                .mangaId("manga-1")
                                .url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                .build())
                                .build();

                when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));

                var result = chapterService.getChapterById(chapterId);

                assertEquals(chapter.getId(), result.getId());
                assertEquals(chapter.getNumber(), result.getNumber());
                assertEquals(chapter.getMangaId(), result.getMangaId());
                assertEquals(chapter.getUrl(), result.getUrl());
        }

        // Test getLastChapterNumber

        @Test
        @DisplayName("getLastChapterNumber - Debe retornar 0.0 cuando mangaId es null")
        void testGetLastChapterNumberWithNullMangaId() {
                var result = chapterService.getLastChapterNumber(null);
                assertEquals(0.0, result);
        }

        @Test
        @DisplayName("getLastChapterNumber - Debe retornar 0.0 cuando mangaId está vacío")
        void testGetLastChapterNumberWithEmptyMangaId() {
                var result = chapterService.getLastChapterNumber("");
                assertEquals(0.0, result);
        }

        @Test
        @DisplayName("getLastChapterNumber - Debe retornar 0.0 cuando no hay capítulos en la base de datos y getChapters retorna vacío")
        void testGetLastChapterNumberNoChaptersInDatabaseAndGetChaptersReturnsEmpty() {
                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(List.of());
//                when(mangaService.getMangaById(any(String.class))).thenReturn(null);

                var result = chapterService.getLastChapterNumber("manga-1");
                assertEquals(0.0, result);
        }

        @Test
        @DisplayName("getLastChapterNumber - Debe retornar el número más alto cuando hay capítulos en la base de datos")
        void testGetLastChapterNumberWithChaptersInDatabase() {
                List<ChapterModel> chapters = new ArrayList<>(List.of(
                                ChapterModel.builder().id("1").number(1.0).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(2.5).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("3").number(3.0).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-3")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(chapters);

                var result = chapterService.getLastChapterNumber("manga-1");
                assertEquals(3.0, result);
        }

        @SuppressWarnings({ "null", "unchecked" })
        @Test
        @DisplayName("getLastChapterNumber - Debe retornar el número más alto cuando no hay capítulos en la base de datos pero getChapters encuentra algunos")
        void testGetLastChapterNumberNoChaptersInDatabaseButGetChaptersFindsSome() {
                List<ChapterModel> chapters = new LinkedList<>(List.of(
                                ChapterModel.builder().id("1").number(1.0).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(5.5).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(List.of());
//                when(mangaService.getMangaById(any(String.class)))
//                                .thenReturn(MangaModel.builder().id("manga-1").name("One Piece").url(UrlModel.builder()
//                                                .url("https://example.com/one-piece")
//                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build());
                when(scrapperService.getChapters(any(UrlModel.class))).thenReturn(chapters);
                when(chapterRepository.saveAll(any(List.class))).thenReturn(chapters);

                var result = chapterService.getLastChapterNumber("manga-1");
                assertEquals(5.5, result);
        }

        @Test
        @DisplayName("getLastChapterNumber - Debe filtrar capítulos con number null y retornar el máximo de los válidos")
        void testGetLastChapterNumberFiltersNullNumbers() {
                List<ChapterModel> chapters = new ArrayList<>(List.of(
                                ChapterModel.builder().id("1").number(1.0).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(null).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("3").number(3.0).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-3")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(chapters);

                var result = chapterService.getLastChapterNumber("manga-1");
                assertEquals(3.0, result);
        }

        @Test
        @DisplayName("getLastChapterNumber - Debe retornar 0.0 cuando todos los capítulos tienen number null")
        void testGetLastChapterNumberAllChaptersHaveNullNumber() {
                List<ChapterModel> chapters = new ArrayList<>(List.of(
                                ChapterModel.builder().id("1").number(null).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build(),
                                ChapterModel.builder().id("2").number(null).mangaId("manga-1").url(UrlModel.builder()
                                                .url("https://example.com/chapter-2")
                                                .scrapper(ScrappersEnum.leerCapitulo).build()).build()));

                when(chapterRepository.findByMangaIdOrderByNumberAsc(anyString())).thenReturn(chapters);

                var result = chapterService.getLastChapterNumber("manga-1");
                assertEquals(0.0, result);
        }
}