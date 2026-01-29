package com.manga.server.features.manga;

import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.manga.server.core.filtres.RateLimitFilter;
import com.manga.server.features.manga.controller.MangaControllerV1;
import com.manga.server.features.manga.dtos.MangaDTO;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.services.MangaService;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@WebMvcTest(MangaControllerV1.class)
@Import(RateLimitFilter.class)
class MangaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private MangaService mangaService;

    @MockitoBean
    private MangaMapper mangaMapper;

    private MangaModel mangaModel1;
    private MangaModel mangaModel2;
    private MangaDTO mangaDTO1;
    private MangaDTO mangaDTO2;
    private List<MangaModel> mangaModels;
    private List<MangaDTO> mangaDTOs;

    @BeforeEach
    void setUp() {
        // Limpiar los buckets de rate limiting antes de cada test
        if (rateLimitFilter != null) {
            rateLimitFilter.reset();
        }

        mangaModel1 = MangaModel.builder()
                .id("1")
                .name("One Piece")
                .thumbnail(UrlModel.builder().url("https://example.com/one-piece.jpg").scrapper(ScrappersEnum.leerCapitulo).build())
                .description("Aventuras piratas")
                .lastChapter(1100.0)
                .build();

        mangaModel2 = MangaModel.builder()
                .id("2")
                .name("Naruto")
                .thumbnail(UrlModel.builder().url("https://example.com/naruto.jpg").scrapper(ScrappersEnum.leerCapitulo).build())
                .description("Ninja shinobi")
                .lastChapter(700.0)
                .build();

        mangaDTO1 = new MangaDTO("1", "One Piece", "https://example.com/one-piece.jpg", 1100.0, "Aventuras piratas");
        mangaDTO2 = new MangaDTO("2", "Naruto", "https://example.com/naruto.jpg", 700.0, "Ninja shinobi");

        mangaModels = Arrays.asList(mangaModel1, mangaModel2);
        mangaDTOs = Arrays.asList(mangaDTO1, mangaDTO2);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET / - Debe retornar lista de mangas con nuevos capítulos")
    void testGetMangasWhitNewChapters() throws Exception {
        // Given
        org.mockito.Mockito.when(mangaService.mangasWithNewChapters()).thenReturn(mangaModels);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(mangaModels)).thenReturn(mangaDTOs);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("One Piece"))
                .andExpect(jsonPath("$[0].lastChapter").value(1100.0))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Naruto"))
                .andExpect(jsonPath("$[1].lastChapter").value(700.0));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /search?query=One - Debe retornar mangas que coincidan con la búsqueda")
    void testSearchMangas() throws Exception {
        // Given
        String query = "One";
        List<MangaModel> searchResults = Arrays.asList(mangaModel1);
        List<MangaDTO> searchDTOs = Arrays.asList(mangaDTO1);

        org.mockito.Mockito.when(mangaService.searchManga(query)).thenReturn(searchResults);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(searchResults)).thenReturn(searchDTOs);

        // When & Then
        mockMvc.perform(get("/search")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("One Piece"));
    }

    @Test
    @DisplayName("GET /search - Debe retornar 400 cuando falta el parámetro query")
    void testSearchMangasWithoutQuery() throws Exception {
        // When & Then
        mockMvc.perform(get("/search"))
                .andExpect(status().isBadRequest());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /ids?ids=1&ids=2 - Debe retornar mangas por IDs")
    void testGetMangasByIds() throws Exception {
        // Given
        List<String> ids = Arrays.asList("1", "2");
        org.mockito.Mockito.when(mangaService.getMangasByIds(ids)).thenReturn(mangaModels);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(mangaModels)).thenReturn(mangaDTOs);

        // When & Then
        mockMvc.perform(get("/ids")
                .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @DisplayName("GET /ids - Debe retornar 400 cuando no se proporcionan IDs")
    void testGetMangasByIdsEmpty() throws Exception {
        // When & Then
        mockMvc.perform(get("/ids"))
                .andExpect(status().isBadRequest());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET / - Debe retornar lista vacía cuando no hay mangas con nuevos capítulos")
    void testGetMangasWhitNewChaptersEmpty() throws Exception {
        // Given
        org.mockito.Mockito.when(mangaService.mangasWithNewChapters()).thenReturn(Arrays.asList());
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(anyList())).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Test Rate Limiting

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET / - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
    void testRateLimitGetMangasWhitNewChapters() throws Exception {
        // Given
        org.mockito.Mockito.when(mangaService.mangasWithNewChapters()).thenReturn(mangaModels);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(mangaModels)).thenReturn(mangaDTOs);

        // When & Then - Primeros 5 requests deben ser exitosos
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk());
        }

        // El 6to request debe ser rechazado con 429
        mockMvc.perform(get("/"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /search - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
    void testRateLimitSearchMangas() throws Exception {
        // Given
        String query = "One";
        List<MangaModel> searchResults = Arrays.asList(mangaModel1);
        List<MangaDTO> searchDTOs = Arrays.asList(mangaDTO1);

        org.mockito.Mockito.when(mangaService.searchManga(query)).thenReturn(searchResults);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(searchResults)).thenReturn(searchDTOs);

        // When & Then - Primeros 5 requests deben ser exitosos
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/search")
                    .param("query", query))
                    .andExpect(status().isOk());
        }

        // El 6to request debe ser rechazado con 429
        mockMvc.perform(get("/search")
                .param("query", query))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("GET /ids - Rate limit: Debe permitir 5 requests y rechazar el 6to con 429")
    void testRateLimitGetMangasByIds() throws Exception {
        // Given
        List<String> ids = Arrays.asList("1", "2");
        org.mockito.Mockito.when(mangaService.getMangasByIds(ids)).thenReturn(mangaModels);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(mangaModels)).thenReturn(mangaDTOs);

        // When & Then - Primeros 5 requests deben ser exitosos
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/ids")
                    .param("ids", "1", "2"))
                    .andExpect(status().isOk());
        }

        // El 6to request debe ser rechazado con 429
        mockMvc.perform(get("/ids")
                .param("ids", "1", "2"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Rate limit excedido. Máximo 5 requests por minuto por endpoint."));
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Rate limit: Cada endpoint debe tener su propio límite independiente")
    void testRateLimitIndependentPerEndpoint() throws Exception {
        // Given
        org.mockito.Mockito.when(mangaService.mangasWithNewChapters()).thenReturn(mangaModels);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(mangaModels)).thenReturn(mangaDTOs);

        String query = "One";
        List<MangaModel> searchResults = Arrays.asList(mangaModel1);
        List<MangaDTO> searchDTOs = Arrays.asList(mangaDTO1);
        org.mockito.Mockito.when(mangaService.searchManga(query)).thenReturn(searchResults);
        org.mockito.Mockito.when(mangaMapper.mangasToMangaDTOs(searchResults)).thenReturn(searchDTOs);

        // When & Then - Hacer 5 requests a "/" y 5 requests a "/search"
        // Todos deben ser exitosos porque cada endpoint tiene su propio límite
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/search")
                    .param("query", query))
                    .andExpect(status().isOk());
        }

        // El 6to request a "/" debe ser rechazado
        mockMvc.perform(get("/"))
                .andExpect(status().isTooManyRequests());

        // Pero el 6to request a "/search" también debe ser rechazado
        mockMvc.perform(get("/search")
                .param("query", query))
                .andExpect(status().isTooManyRequests());
    }
}

