package com.manga.server.features.manga.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.services.ScrapperService;

@ExtendWith(MockitoExtension.class)
class MangaServiceTest {

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private MangaRepository mangaRepository;

    @Mock
    private ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;

    @Mock
    private java.util.concurrent.Executor executor;

    @InjectMocks
    private MangaService mangaService;

    private MangaModel mangaModel1;
    private MangaModel mangaModel2;
    private List<MangaModel> mangaModels;

    @BeforeEach
    void setUp() {
        mangaModel1 = MangaModel.builder()
                .id("1")
                .name("One Piece")
                .url("https://example.com/one-piece")
                .thumbnail("https://example.com/one-piece.jpg")
                .description("Aventuras piratas")
                .lastChapter(1100.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        mangaModel2 = MangaModel.builder()
                .id("2")
                .name("Naruto")
                .url("https://example.com/naruto")
                .thumbnail("https://example.com/naruto.jpg")
                .description("Ninja shinobi")
                .lastChapter(700.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        mangaModels = Arrays.asList(mangaModel1, mangaModel2);
    }

    @Test
    @DisplayName("getMangasByIds - Debe retornar lista de mangas cuando se proporcionan IDs válidos")
    void testGetMangasByIds() {
        // Given
        List<String> ids = Arrays.asList("1", "2");
        when(mangaRepository.findAllById(ids)).thenReturn(mangaModels);

        // When
        List<MangaModel> result = mangaService.getMangasByIds(ids);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("One Piece", result.get(0).getName());
        assertEquals("Naruto", result.get(1).getName());
        verify(mangaRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("getMangasByIds - Debe retornar lista vacía cuando no hay IDs")
    void testGetMangasByIdsEmpty() {
        // Given
        List<String> ids = Arrays.asList();
        when(mangaRepository.findAllById(ids)).thenReturn(Arrays.asList());

        // When
        List<MangaModel> result = mangaService.getMangasByIds(ids);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(mangaRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("getMangaById - Debe retornar manga cuando existe con el ID")
    void testGetMangaByIdExists() {
        // Given
        String mangaId = "1";
        when(mangaRepository.findById(mangaId)).thenReturn(Optional.of(mangaModel1));

        // When
        MangaModel result = mangaService.getMangaById(mangaId);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("One Piece", result.getName());
        verify(mangaRepository, times(1)).findById(mangaId);
    }

    @Test
    @DisplayName("getMangaById - Debe retornar null cuando no existe el manga")
    void testGetMangaByIdNotExists() {
        // Given
        String mangaId = "999";
        when(mangaRepository.findById(mangaId)).thenReturn(Optional.empty());

        // When
        MangaModel result = mangaService.getMangaById(mangaId);

        // Then
        assertNull(result);
        verify(mangaRepository, times(1)).findById(mangaId);
    }

    @Test
    @DisplayName("mangasWithNewChapters - Debe retornar lista de mangas con nuevos capítulos")
    void testMangasWithNewChapters() {
        // Given
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(listOfMangasWhitNewChapterService.getLastListNewManga(scrapper)).thenReturn(mangaModels);

        // When
        List<MangaModel> result = mangaService.mangasWithNewChapters();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(listOfMangasWhitNewChapterService, times(1)).getLastListNewManga(scrapper);
    }

    @Test
    @DisplayName("mangasWithNewChapters - Debe retornar null cuando no hay lista disponible")
    void testMangasWithNewChaptersNull() {
        // Given
        ScrappersEnum scrapper = ScrappersEnum.leerCapitulo;
        when(listOfMangasWhitNewChapterService.getLastListNewManga(scrapper)).thenReturn(null);

        // When
        List<MangaModel> result = mangaService.mangasWithNewChapters();

        // Then
        assertNull(result);
        verify(listOfMangasWhitNewChapterService, times(1)).getLastListNewManga(scrapper);
    }

    @Test
    @DisplayName("searchManga - Debe retornar mangas de la base de datos cuando encuentra resultados")
    void testSearchMangaFoundInDatabase() {
        // Given
        String query = "One";
        // Más de 3 resultados para que no busque en el scrapper
        List<MangaModel> dbResults = Arrays.asList(mangaModel1, mangaModel2, mangaModel1, mangaModel2);
        when(mangaRepository.findAll(any(Example.class))).thenReturn(dbResults);

        // When
        List<MangaModel> result = mangaService.searchManga(query);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(mangaRepository, times(1)).findAll(any(Example.class));
        verify(scrapperService, never()).searchManga(any(ScrappersEnum.class), anyString());
    }

    @Test
    @DisplayName("searchManga - Debe buscar en scrapper cuando hay pocos resultados en BD")
    void testSearchMangaWithScrapper() {
        // Given
        String query = "Bleach";
        // Usar ArrayList para que sea mutable
        List<MangaModel> dbResults = new ArrayList<>(); // Pocos resultados
        MangaModel scrapperManga = MangaModel.builder()
                .name("Bleach")
                .url("https://example.com/bleach")
                .build();
        List<MangaModel> scrapperResults = Arrays.asList(scrapperManga);

        when(mangaRepository.findAll(any(Example.class))).thenReturn(dbResults);
        when(scrapperService.searchManga(ScrappersEnum.leerCapitulo, query)).thenReturn(scrapperResults);
        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(mangaRepository.save(any(MangaModel.class))).thenReturn(scrapperManga);

        // When
        List<MangaModel> result = mangaService.searchManga(query);

        // Then
        assertNotNull(result);
        verify(scrapperService, times(1)).searchManga(ScrappersEnum.leerCapitulo, query);
        verify(mangaRepository, atLeast(1)).save(any(MangaModel.class));
    }

    @Test
    @DisplayName("searchManga - No debe buscar en scrapper cuando hay suficientes resultados en BD")
    void testSearchMangaNotUseScrapper() {
        // Given
        String query = "One";
        // 4 resultados en BD (más de 3)
        List<MangaModel> dbResults = Arrays.asList(mangaModel1, mangaModel2, mangaModel1, mangaModel2);
        when(mangaRepository.findAll(any(Example.class))).thenReturn(dbResults);

        // When
        List<MangaModel> result = mangaService.searchManga(query);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(mangaRepository, times(1)).findAll(any(Example.class));
        verify(scrapperService, never()).searchManga(any(), anyString());
    }

    @Test
    @DisplayName("searchManga - No debe agregar mangas duplicados del scrapper")
    void testSearchMangaAvoidDuplicates() {
        // Given
        String query = "One";
        List<MangaModel> dbResults = Arrays.asList(mangaModel1); // 1 resultado
        // El scrapper devuelve el mismo manga que ya está en BD
        List<MangaModel> scrapperResults = Arrays.asList(mangaModel1);

        when(mangaRepository.findAll(any(Example.class))).thenReturn(dbResults);
        when(scrapperService.searchManga(ScrappersEnum.leerCapitulo, query)).thenReturn(scrapperResults);

        // When
        List<MangaModel> result = mangaService.searchManga(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // No se debe duplicar
        verify(scrapperService, times(1)).searchManga(ScrappersEnum.leerCapitulo, query);
        verify(mangaRepository, never()).save(any(MangaModel.class)); // No debe guardar duplicado
    }
}

