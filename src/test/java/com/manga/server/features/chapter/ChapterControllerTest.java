package com.manga.server.features.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.manga.server.core.filtres.RateLimitFilter;
import com.manga.server.features.chapter.controller.ChapterControllerV1;
import com.manga.server.features.chapter.requests.ChapterDTO;
import com.manga.server.features.chapter.requests.ImgDTO;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.chapter.mappers.ImgMapper;
import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.chapter.services.ChapterService;
import com.manga.server.features.chapter.services.ImgService;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChapterControllerV1.class)
@Import(RateLimitFilter.class)
class ChapterControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private RateLimitFilter rateLimitFilter;

        @MockitoBean
        private ChapterService chapterService;

        @MockitoBean
        private ChapterMapper chapterMapper;

        @MockitoBean
        private ImgService imgService;

        @MockitoBean
        private ImgMapper imgMapper;

        @BeforeEach
        void setUp() {
                // Limpiar los buckets de rate limiting antes de cada test
                if (rateLimitFilter != null) {
                        rateLimitFilter.reset();
                }
        }

        // Get Chapters

        @Test
        @DisplayName("GET /chapter - Debe retornar 400 cuando no se proporciona el mangaId")
        void testGetChaptersBadRequest() throws Exception {

                mockMvc.perform(get("/chapter"))
                                .andExpect(status().isBadRequest());
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter - Debe retornar lista vacía de capítulos de un manga")
        void testGetChaptersEmpty() throws Exception {

                // Arrange
                List<ChapterModel> chapterModels = List.of();
                List<ChapterDTO> chapterDTOs = List.of();

                Mockito.when(chapterService.getChapters("1")).thenReturn(chapterModels);
                Mockito.when(chapterMapper.chaptersToChapterDTOs(chapterModels)).thenReturn(chapterDTOs);

                // Act & Assert
                mockMvc.perform(get("/chapter")
                                .param("mangaId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray());

        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter - Debe retornar lista de capítulos de un manga")
        void testGetChapters() throws Exception {

                // Arrange
                List<ChapterModel> chapterModels = List.of(
                                ChapterModel.builder()
                                                .id("1")
                                                .number(1.0)
                                                .build(),
                                ChapterModel.builder()
                                                .id("2")
                                                .number(2.0)
                                                .build());
                List<ChapterDTO> chapterDTOs = List.of(
                                new ChapterDTO("1", 1.0),
                                new ChapterDTO("2", 2.0));

                Mockito.when(chapterService.getChapters("1")).thenReturn(chapterModels);
                Mockito.when(chapterMapper.chaptersToChapterDTOs(chapterModels)).thenReturn(chapterDTOs);

                mockMvc.perform(get("/chapter")
                                .param("mangaId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value("1"))
                                .andExpect(jsonPath("$[0].number").value(1.0))
                                .andExpect(jsonPath("$[1].id").value("2"))
                                .andExpect(jsonPath("$[1].number").value(2.0));
        }

        // Get Images+
        @Test
        @DisplayName("GET /chapter/img - Debe retornar 400 cuando no se proporciona el chapterId")
        void testGetImgBadRequest() throws Exception {
                mockMvc.perform(get("/chapter/img"))
                                .andExpect(status().isBadRequest());
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter/img - Debe retornar lista vacía de imágenes de un capítulo")
        void testGetImgEmpty() throws Exception {
                // Arrange
                List<ImgModel> imgModels = List.of();
                List<ImgDTO> imgDTOs = List.of();

                Mockito.when(imgService.getImg("1")).thenReturn(imgModels);
                Mockito.when(imgMapper.imgModelsToImgDTOs(imgModels)).thenReturn(imgDTOs);

                mockMvc.perform(get("/chapter/img")
                                .param("chapterId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter/img - Debe retornar lista de imágenes de un capítulo")
        void testGetImg() throws Exception {
                // Arrange
                List<ImgModel> imgModels = List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/image1")
                                                                .build())
                                                .build(),
                                ImgModel.builder()
                                                .id("2")
                                                .number(2)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/image2")
                                                                .build())
                                                .build());

                List<ImgDTO> imgDTOs = List.of(
                                new ImgDTO("1", 1, "https://example.com/image1"),
                                new ImgDTO("2", 2, "https://example.com/image2"));

                Mockito.when(imgService.getImg("1")).thenReturn(imgModels);
                Mockito.when(imgMapper.imgModelsToImgDTOs(imgModels)).thenReturn(imgDTOs);

                mockMvc.perform(get("/chapter/img")
                                .param("chapterId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value("1"))
                                .andExpect(jsonPath("$[0].number").value(1))
                                .andExpect(jsonPath("$[0].url").value("https://example.com/image1"))
                                .andExpect(jsonPath("$[1].id").value("2"))
                                .andExpect(jsonPath("$[1].number").value(2))
                                .andExpect(jsonPath("$[1].url").value("https://example.com/image2"));
        }

        // Preload Images
        @Test
        @DisplayName("GET /chapter/img/preload - Debe retornar 400 cuando no se proporcionan los chapterIds")
        void testPreloadImagesBadRequest() throws Exception {
                mockMvc.perform(get("/chapter/img/preload"))
                                .andExpect(status().isBadRequest());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("GET /chapter/img/preload - Debe retornar 200 cuando se proporcionan los chapterIds")
        void testPreloadImages() throws Exception {

                List<String> chapterIds = List.of("1", "2");

                mockMvc.perform(get("/chapter/img/preload")
                                .param("chapterIds", "1", "2"))
                                .andExpect(status().isOk());

                ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                Mockito.verify(imgService, times(1)).preloadImages(argumentCaptor.capture());

                assertEquals(chapterIds, argumentCaptor.getValue());
        }

        // Test Rate Limiting

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
        void testRateLimitGetChapters() throws Exception {
                // Given
                List<ChapterModel> chapterModels = List.of(
                                ChapterModel.builder()
                                                .id("1")
                                                .number(1.0)
                                                .build());
                List<ChapterDTO> chapterDTOs = List.of(new ChapterDTO("1", 1.0));

                Mockito.when(chapterService.getChapters("1")).thenReturn(chapterModels);
                Mockito.when(chapterMapper.chaptersToChapterDTOs(chapterModels)).thenReturn(chapterDTOs);

                // When & Then - Primeros 5 requests deben ser exitosos
                for (int i = 0; i < 5; i++) {
                        mockMvc.perform(get("/chapter")
                                        .param("mangaId", "1"))
                                        .andExpect(status().isOk());
                }

                // El 6to request debe ser rechazado con 429
                mockMvc.perform(get("/chapter")
                                .param("mangaId", "1"))
                                .andExpect(status().isTooManyRequests())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.error").value(
                                                "Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter/img - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
        void testRateLimitGetImg() throws Exception {
                // Given
                List<ImgModel> imgModels = List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/image1")
                                                                .build())
                                                .build());
                List<ImgDTO> imgDTOs = List.of(new ImgDTO("1", 1, "https://example.com/image1"));

                Mockito.when(imgService.getImg("1")).thenReturn(imgModels);
                Mockito.when(imgMapper.imgModelsToImgDTOs(imgModels)).thenReturn(imgDTOs);

                // When & Then - Primeros 5 requests deben ser exitosos
                for (int i = 0; i < 5; i++) {
                        mockMvc.perform(get("/chapter/img")
                                        .param("chapterId", "1"))
                                        .andExpect(status().isOk());
                }

                // El 6to request debe ser rechazado con 429
                mockMvc.perform(get("/chapter/img")
                                .param("chapterId", "1"))
                                .andExpect(status().isTooManyRequests())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.error").value(
                                                "Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("GET /chapter/img/preload - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
        void testRateLimitPreloadImages() throws Exception {
                // When & Then - Primeros 5 requests deben ser exitosos
                for (int i = 0; i < 5; i++) {
                        mockMvc.perform(get("/chapter/img/preload")
                                        .param("chapterIds", "1", "2"))
                                        .andExpect(status().isOk());
                }

                // El 6to request debe ser rechazado con 429
                mockMvc.perform(get("/chapter/img/preload")
                                .param("chapterIds", "1", "2"))
                                .andExpect(status().isTooManyRequests())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.error").value(
                                                "Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
        }

        @SuppressWarnings("null")
        @Test
        @DisplayName("Rate limit: Cada endpoint debe tener su propio límite independiente")
        void testRateLimitIndependentPerEndpoint() throws Exception {
                // Given
                List<ChapterModel> chapterModels = List.of(
                                ChapterModel.builder()
                                                .id("1")
                                                .number(1.0)
                                                .build());
                List<ChapterDTO> chapterDTOs = List.of(new ChapterDTO("1", 1.0));

                List<ImgModel> imgModels = List.of(
                                ImgModel.builder()
                                                .id("1")
                                                .number(1)
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/image1")
                                                                .build())
                                                .build());
                List<ImgDTO> imgDTOs = List.of(new ImgDTO("1", 1, "https://example.com/image1"));

                Mockito.when(chapterService.getChapters("1")).thenReturn(chapterModels);
                Mockito.when(chapterMapper.chaptersToChapterDTOs(chapterModels)).thenReturn(chapterDTOs);
                Mockito.when(imgService.getImg("1")).thenReturn(imgModels);
                Mockito.when(imgMapper.imgModelsToImgDTOs(imgModels)).thenReturn(imgDTOs);

                // When & Then - Hacer 5 requests a "/chapter" y 5 requests a "/chapter/img"
                // Todos deben ser exitosos porque cada endpoint tiene su propio límite
                for (int i = 0; i < 5; i++) {
                        mockMvc.perform(get("/chapter")
                                        .param("mangaId", "1"))
                                        .andExpect(status().isOk());
                        mockMvc.perform(get("/chapter/img")
                                        .param("chapterId", "1"))
                                        .andExpect(status().isOk());
                }

                // El 6to request a "/chapter" debe ser rechazado
                mockMvc.perform(get("/chapter")
                                .param("mangaId", "1"))
                                .andExpect(status().isTooManyRequests());

                // Pero el 6to request a "/chapter/img" también debe ser rechazado
                mockMvc.perform(get("/chapter/img")
                                .param("chapterId", "1"))
                                .andExpect(status().isTooManyRequests());
        }

}
