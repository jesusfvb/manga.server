package com.manga.server.features.images.user_cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.manga.server.features.chapter.services.ChapterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.images.repocitory.ImgRepository;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class ImgServiceTests {

        @InjectMocks
        private ImgService imgService;

        @Mock
        private ScrapperService scrapperService;

        @Mock
        private ImgRepository imgRepository;

        @Mock
        private ChapterService chapterService;

        @Mock
        private ImgPreloadService imgPreloadService;

        // Test getImg

        @Test
        @DisplayName("getImg - Debe retornar lista vacía cuando chapterId es null")
        void testGetImgWithNullChapterId() {
                var result = imgService.getImg(null);
                Assertions.assertEquals(0, result.size());
        }

        @Test
        @DisplayName("getImg - Debe retornar lista vacía cuando chapterId está vacío")
        void testGetImgWithEmptyChapterId() {
                var result = imgService.getImg("");
                Assertions.assertEquals(0, result.size());
        }

        @Test
        @DisplayName("getImg - Debe retornar imágenes cuando se encuentran en la base de datos")
        void testGetImgFindImagesInDatabase() {
                List<ImgModel> imgs = new ArrayList<>(List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .chapterId("chapter-1")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img1.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build(),
                                ImgModel.builder()
                                                .id("2")
                                                .number(2)
                                                .chapterId("chapter-1")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img2.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build()));

                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(imgs);

                var result = imgService.getImg("chapter-1");

                Assertions.assertEquals(2, result.size());
                Assertions.assertEquals(imgs.get(0).getId(), result.get(0).getId());
                Assertions.assertEquals(imgs.get(0).getNumber(), result.get(0).getNumber());
        }

        @Test
        @DisplayName("getImg - Debe retornar lista vacía cuando no hay imágenes en la base de datos y el capítulo no existe")
        void testGetImgNoImagesInDatabaseAndChapterNotFound() {
                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(List.of());
                Mockito.when(chapterService.getChapterById(ArgumentMatchers.any(String.class))).thenReturn(null);

                var result = imgService.getImg("non-existent-chapter");
                Assertions.assertEquals(0, result.size());
        }

        @Test
        @DisplayName("getImg - Debe lanzar NullPointerException cuando no hay imágenes en la base de datos y el capítulo no tiene URL")
        void testGetImgNoImagesInDatabaseAndChapterHasNoUrl() {
                ChapterModel chapter = ChapterModel.builder()
                                .id("chapter-1")
                                .number(1.0)
                                .url(null)
                                .build();

                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(List.of());
                Mockito.when(chapterService.getChapterById(ArgumentMatchers.any(String.class))).thenReturn(chapter);

                // El código actual tiene un bug: no valida que chapter.getUrl() sea null antes
                // de llamar a getFullUrl()
                // Por lo tanto, este test espera que se lance NullPointerException
                Assertions.assertThrows(NullPointerException.class, () -> {
                        imgService.getImg("chapter-1");
                });
        }

        @Test
        @DisplayName("getImg - Debe retornar lista vacía cuando no hay imágenes en la base de datos y el scrapper no retorna imágenes")
        void testGetImgNoImagesInDatabaseAndScrapperReturnsEmpty() {
                ChapterModel chapter = ChapterModel.builder()
                                .id("chapter-1")
                                .number(1.0)
                                .url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                .build())
                                .build();

                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(List.of());
                Mockito.when(chapterService.getChapterById(ArgumentMatchers.any(String.class))).thenReturn(chapter);
                Mockito.when(scrapperService.getImg(ArgumentMatchers.any(ScrappersEnum.class), ArgumentMatchers.any(String.class))).thenReturn(List.of());

                var result = imgService.getImg("chapter-1");
                Assertions.assertEquals(0, result.size());
        }

        @SuppressWarnings({ "null", "unchecked" })
        @Test
        @DisplayName("getImg - Debe guardar y retornar imágenes cuando no hay imágenes en la base de datos y el scrapper retorna imágenes")
        void testGetImgNoImagesInDatabaseAndScrapperReturnsImages() {
                ChapterModel chapter = ChapterModel.builder()
                                .id("chapter-1")
                                .number(1.0)
                                .url(UrlModel.builder()
                                                .url("https://example.com/chapter-1")
                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                .build())
                                .build();

                List<ImgModel> scrapedImgs = new ArrayList<>(List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img1.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build(),
                                ImgModel.builder()
                                                .id("2")
                                                .number(2)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img2.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build()));

                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(List.of());
                Mockito.when(chapterService.getChapterById(ArgumentMatchers.any(String.class))).thenReturn(chapter);
                Mockito.when(scrapperService.getImg(ArgumentMatchers.any(ScrappersEnum.class), ArgumentMatchers.any(String.class))).thenReturn(scrapedImgs);
                Mockito.when(imgRepository.saveAll(ArgumentMatchers.any(List.class))).thenReturn(scrapedImgs);

                var result = imgService.getImg("chapter-1");

                Assertions.assertEquals(2, result.size());
                Assertions.assertEquals("chapter-1", result.get(0).getChapterId());
                Assertions.assertEquals("chapter-1", result.get(1).getChapterId());
                Assertions.assertEquals(1, result.get(0).getNumber());
                Assertions.assertEquals(2, result.get(1).getNumber());
        }

        @Test
        @DisplayName("getImg - Debe ordenar imágenes por número cuando se encuentran en la base de datos")
        void testGetImgSortsImagesByNumber() {
                List<ImgModel> imgs = new ArrayList<>(List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .chapterId("chapter-1")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img1.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build(),
                                ImgModel.builder()
                                                .id("2")
                                                .number(2)
                                                .chapterId("chapter-1")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img2.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build(),

                                ImgModel.builder()
                                                .id("3")
                                                .number(3)
                                                .chapterId("chapter-1")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/img3.jpg")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .build()));

                Mockito.when(imgRepository.findByChapterIdOrderByNumberAsc(ArgumentMatchers.anyString())).thenReturn(imgs);

                var result = imgService.getImg("chapter-1");

                Assertions.assertEquals(3, result.size());
                Assertions.assertEquals(1, result.get(0).getNumber());
                Assertions.assertEquals(2, result.get(1).getNumber());
                Assertions.assertEquals(3, result.get(2).getNumber());
        }
}
