package com.manga.server.features.manga.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class MangaSaveServiceTests {

    @InjectMocks
    private MangaSaveService mangaSaveService;

    @Mock
    private MangaRepository mangaRepository;

    // Test saveIfNotExists(MangaModel)

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - No debe hacer nada cuando manga es null")
    void testSaveIfNotExistsWithNullManga() {
        mangaSaveService.saveIfNotExists((MangaModel) null);

        verify(mangaRepository, never()).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - Debe guardar el manga cuando no existe en la base de datos")
    void testSaveIfNotExistsWhenMangaNotExists() {
        MangaModel manga = MangaModel.builder()
                .name("One Piece")
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .lastChapter(1100.0)
                .build();

        MangaModel savedManga = MangaModel.builder()
                .id("saved-id")
                .name("One Piece")
                .url(manga.getUrl())
                .lastChapter(1100.0)
                .build();

        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(mangaRepository.save(any(MangaModel.class))).thenReturn(savedManga);

        mangaSaveService.saveIfNotExists(manga);

        verify(mangaRepository).findOne(any(Example.class));
        verify(mangaRepository).save(argThat(
                model -> model.getId().equals("saved-id")
                        && model.getName().equals("One Piece")
                        && model.getUrl().equals(manga.getUrl())
                        && model.getLastChapter().equals(1100.0)));

    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - No debe hacer nada cuando el manga ya existe en la base de datos")
    void testSaveIfNotExistsWhenMangaExists() {
        MangaModel manga = MangaModel.builder()
                .name("One Piece")
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .lastChapter(1100.0)
                .build();

        MangaModel existingManga = MangaModel.builder()
                .id("existing-id")
                .name("One Piece")
                .url(manga.getUrl())
                .lastChapter(1100.0)
                .build();

        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.of(existingManga));

        mangaSaveService.saveIfNotExists(manga);

        verify(mangaRepository).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - No debe hacer nada cuando el nombre del manga es null")
    void testSaveIfNotExistsWhenMangaNameIsNull() {
        MangaModel manga = MangaModel.builder()
                .name(null)
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .build();

        mangaSaveService.saveIfNotExists(manga);

        verify(mangaRepository, never()).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - No debe hacer nada cuando la url del manga es null")
    void testSaveIfNotExistsWhenMangaUrlIsNull() {
        MangaModel manga = MangaModel.builder()
                .name("One Piece")
                .url(null)
                .lastChapter(1100.0)
                .build();

        mangaSaveService.saveIfNotExists(manga);

        verify(mangaRepository, never()).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(MangaModel) - Debe guardar lastChapter como 0.0 cuando es null")
    void testSaveIfNotExistsWhenMangaLastChapterIsNull() {
        MangaModel manga = MangaModel.builder()
                .name("One Piece")
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .lastChapter(null)
                .build();

        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(mangaRepository.save(any(MangaModel.class))).thenReturn(manga);

        mangaSaveService.saveIfNotExists(manga);

        verify(mangaRepository).save(argThat(
                model -> model.getLastChapter().equals(0.0)));
    }
    // Test saveIfNotExists(List<MangaModel>)

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(List) - No debe hacer nada cuando la lista es null")
    void testSaveIfNotExistsWithNullList() {
        mangaSaveService.saveIfNotExists((List<MangaModel>) null);

        verify(mangaRepository, never()).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(List) - No debe hacer nada cuando la lista está vacía")
    void testSaveIfNotExistsWithEmptyList() {
        mangaSaveService.saveIfNotExists(List.of());

        verify(mangaRepository, never()).findOne(any(Example.class));
        verify(mangaRepository, never()).save(any(MangaModel.class));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(List) - Debe guardar todos los mangas cuando no existen")
    void testSaveIfNotExistsWithListWhenMangasNotExist() {
        MangaModel manga1 = MangaModel.builder()
                .name("One Piece")
                .url(UrlModel.builder()
                        .url("https://example.com/one-piece")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .build();

        MangaModel manga2 = MangaModel.builder()
                .name("Naruto")
                .url(UrlModel.builder()
                        .url("https://example.com/naruto")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .build();

        List<MangaModel> mangas = List.of(manga1, manga2);

        MangaModel savedManga1 = MangaModel.builder()
                .id("id-1")
                .name("One Piece")
                .url(manga1.getUrl())
                .build();

        MangaModel savedManga2 = MangaModel.builder()
                .id("id-2")
                .name("Naruto")
                .url(manga2.getUrl())
                .build();

        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(mangaRepository.save(manga1)).thenReturn(savedManga1);
        when(mangaRepository.save(manga2)).thenReturn(savedManga2);

        mangaSaveService.saveIfNotExists(mangas);

        verify(mangaRepository, times(2)).findOne(any(Example.class));

        verify(mangaRepository).save(argThat(
                model -> model.getId().equals("id-1")
                        && model.getName().equals("One Piece")
                        && model.getUrl().equals(manga1.getUrl())
                        && model.getLastChapter().equals(0.0)));
        verify(mangaRepository).save(argThat(
                model -> model.getId().equals("id-2")
                        && model.getName().equals("Naruto")
                        && model.getUrl().equals(manga2.getUrl())
                        && model.getLastChapter().equals(0.0)));
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    @DisplayName("saveIfNotExists(List) - Debe manejar mangas null en la lista")
    void testSaveIfNotExistsWithListContainingNullManga() {
        MangaModel validManga = MangaModel.builder()
                .name("Valid Manga")
                .url(UrlModel.builder()
                        .url("https://example.com/valid-manga")
                        .scrapper(ScrappersEnum.leerCapitulo)
                        .build())
                .build();

        List<MangaModel> mangas = new ArrayList<>(Arrays.asList(validManga, null));

        MangaModel savedManga = MangaModel.builder()
                .id("saved-id")
                .name("Valid Manga")
                .url(validManga.getUrl())
                .build();

        when(mangaRepository.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(mangaRepository.save(validManga)).thenReturn(savedManga);

        mangaSaveService.saveIfNotExists(mangas);

        // Solo debe procesar el manga válido
        verify(mangaRepository, times(1)).findOne(any(Example.class));
        verify(mangaRepository).save(argThat(
                model -> model.getId().equals("saved-id")
                        && model.getName().equals("Valid Manga")
                        && model.getUrl().equals(validManga.getUrl())
                        && model.getLastChapter().equals(0.0)));
    }
}
