package com.manga.server.features.manga.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class MangaUpdateServiceTests {

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;

    @Mock
    private MangaSaveService mangaSaveService;

    @Mock
    private Executor executor;

    @InjectMocks
    private MangaUpdateService mangaUpdateService;

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

        // Configurar executor síncrono para los tests (usar lenient para evitar problemas con stubs innecesarios)
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("updateMangasWithNewChapters - Debe ejecutar correctamente cuando hay mangas nuevos")
    void testUpdateMangasWithNewChaptersSuccess() throws InterruptedException {
        // Given
        when(listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo)).thenReturn(true);
        when(scrapperService.getMangasWithNewChapters()).thenReturn(mangaModels);

        // When
        mangaUpdateService.updateMangasWithNewChapters();

        // Esperar un poco para que el CompletableFuture termine
        Thread.sleep(100);

        // Then
        verify(executor, times(1)).execute(any(Runnable.class));
        verify(listOfMangasWhitNewChapterService, times(1)).isTimeCheck(ScrappersEnum.leerCapitulo);
        verify(scrapperService, times(1)).getMangasWithNewChapters();
        verify(mangaSaveService, times(1)).saveIfNotExists(mangaModels);
        verify(listOfMangasWhitNewChapterService, times(1)).save(mangaModels, ScrappersEnum.leerCapitulo);
    }

    @Test
    @DisplayName("updateMangasWithNewChapters - No debe ejecutar si ya está en proceso")
    void testUpdateMangasWithNewChaptersAlreadyRunning() throws InterruptedException {
        // Given
        // Configurar executor para que no ejecute inmediatamente (simular proceso largo)
        doNothing().when(executor).execute(any(Runnable.class));

        // When - Primera llamada
        mangaUpdateService.updateMangasWithNewChapters();

        // Llamadas mientras la primera está "en proceso"
        mangaUpdateService.updateMangasWithNewChapters();
        mangaUpdateService.updateMangasWithNewChapters();
        mangaUpdateService.updateMangasWithNewChapters();

        // Then
        // Solo debe haber intentado ejecutar una vez por cada llamada
        verify(executor, times(1)).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("updateMangasWithNewChapters - No debe procesar si isTimeCheck retorna false")
    void testUpdateMangasWithNewChaptersTimeCheckFalse() throws InterruptedException {
        // Given
        when(listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo)).thenReturn(false);

        // When
        mangaUpdateService.updateMangasWithNewChapters();

        // Esperar un poco para que el CompletableFuture termine
        Thread.sleep(100);

        // Then
        verify(executor, times(1)).execute(any(Runnable.class));
        verify(listOfMangasWhitNewChapterService, times(1)).isTimeCheck(ScrappersEnum.leerCapitulo);
        verify(scrapperService, never()).getMangasWithNewChapters();
        verify(mangaSaveService, never()).saveIfNotExists(anyList());
        verify(listOfMangasWhitNewChapterService, never()).save(anyList(), any(ScrappersEnum.class));
    }

    @Test
    @DisplayName("updateMangasWithNewChapters - No debe procesar si getMangasWithNewChapters retorna null")
    void testUpdateMangasWithNewChaptersNullMangas() throws InterruptedException {
        // Given
        when(listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo)).thenReturn(true);
        when(scrapperService.getMangasWithNewChapters()).thenReturn(null);

        // When
        mangaUpdateService.updateMangasWithNewChapters();

        // Esperar un poco para que el CompletableFuture termine
        Thread.sleep(100);

        // Then
        verify(executor, times(1)).execute(any(Runnable.class));
        verify(listOfMangasWhitNewChapterService, times(1)).isTimeCheck(ScrappersEnum.leerCapitulo);
        verify(scrapperService, times(1)).getMangasWithNewChapters();
        verify(mangaSaveService, never()).saveIfNotExists(anyList());
        verify(listOfMangasWhitNewChapterService, never()).save(anyList(), any(ScrappersEnum.class));
    }

    @Test
    @DisplayName("updateMangasWithNewChapters - No debe procesar si getMangasWithNewChapters retorna lista vacía")
    void testUpdateMangasWithNewChaptersEmptyList() throws InterruptedException {
        // Given
        when(listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo)).thenReturn(true);
        when(scrapperService.getMangasWithNewChapters()).thenReturn(Collections.emptyList());

        // When
        mangaUpdateService.updateMangasWithNewChapters();

        // Esperar un poco para que el CompletableFuture termine
        Thread.sleep(100);

        // Then
        verify(executor, times(1)).execute(any(Runnable.class));
        verify(listOfMangasWhitNewChapterService, times(1)).isTimeCheck(ScrappersEnum.leerCapitulo);
        verify(scrapperService, times(1)).getMangasWithNewChapters();
        verify(mangaSaveService, never()).saveIfNotExists(anyList());
        verify(listOfMangasWhitNewChapterService, never()).save(anyList(), any(ScrappersEnum.class));
    }

}

