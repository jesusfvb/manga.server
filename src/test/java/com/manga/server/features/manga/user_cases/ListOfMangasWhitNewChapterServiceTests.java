package com.manga.server.features.manga.user_cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.features.manga.model.ListOfMangasWhitNewChapterModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.ListOfMangasWhitNewChapterRepository;
import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

@ExtendWith(MockitoExtension.class)
public class ListOfMangasWhitNewChapterServiceTests {

        @InjectMocks
        private ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;

        @Mock
        private ListOfMangasWhitNewChapterRepository newListMangaRepository;

        // Test isTimeCheck

        @Test
        @DisplayName("isTimeCheck - Debe retornar true cuando scrappersEnum es null")
        void testIsTimeCheckWithNullScrappersEnum() {
                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(null);
                assertTrue(result);
        }

        @Test
        @DisplayName("isTimeCheck - Debe retornar true cuando no se encuentra el modelo en la base de datos")
        void testIsTimeCheckWhenModelNotFound() {
                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.empty());

                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo);
                assertTrue(result);
        }

        @Test
        @DisplayName("isTimeCheck - Debe retornar true cuando el modelo existe pero getDateTime() es null")
        void testIsTimeCheckWhenModelExistsButDateTimeIsNull() {
                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .dateTime(null)
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo);
                assertTrue(result);
        }

        @Test
        @DisplayName("isTimeCheck - Debe retornar true cuando han pasado más de 30 minutos")
        void testIsTimeCheckWhenMoreThan30MinutesHavePassed() {
                // Crear una fecha de hace 31 minutos
                LocalDateTime dateTime = LocalDateTime.now().minusMinutes(31);

                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .dateTime(dateTime)
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo);
                assertTrue(result);
        }

        @Test
        @DisplayName("isTimeCheck - Debe retornar false cuando NO han pasado más de 30 minutos")
        void testIsTimeCheckWhenLessThan30MinutesHavePassed() {
                // Crear una fecha de hace 15 minutos
                LocalDateTime dateTime = LocalDateTime.now().minusMinutes(15);

                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .dateTime(dateTime)
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo);
                assertFalse(result);
        }

        @Test
        @DisplayName("isTimeCheck - Debe retornar false cuando han pasado exactamente 30 minutos")
        void testIsTimeCheckWhenExactly30MinutesHavePassed() {
                // Crear una fecha de hace exactamente 30 minutos
                LocalDateTime dateTime = LocalDateTime.now().minusMinutes(30);

                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .dateTime(dateTime)
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                Boolean result = listOfMangasWhitNewChapterService.isTimeCheck(ScrappersEnum.leerCapitulo);
                assertFalse(result);
        }

        // Test getLastListNewManga

        @Test
        @DisplayName("getLastListNewManga - Debe retornar null cuando scrappersEnum es null")
        void testGetLastListNewMangaWithNullScrappersEnum() {
                List<MangaModel> result = listOfMangasWhitNewChapterService.getLastListNewManga(null);
                assertNull(result);
        }

        @Test
        @DisplayName("getLastListNewManga - Debe retornar null cuando no se encuentra el modelo en la base de datos")
        void testGetLastListNewMangaWhenModelNotFound() {
                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.empty());

                List<MangaModel> result = listOfMangasWhitNewChapterService
                                .getLastListNewManga(ScrappersEnum.leerCapitulo);
                assertNull(result);
        }

        @Test
        @DisplayName("getLastListNewManga - Debe retornar la lista de mangas cuando el modelo existe y tiene mangas")
        void testGetLastListNewMangaWhenModelExistsWithMangas() {
                List<MangaModel> mangas = List.of(
                                MangaModel.builder()
                                                .id("1")
                                                .name("One Piece")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/one-piece")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .lastChapter(1100.0)
                                                .build(),
                                MangaModel.builder()
                                                .id("2")
                                                .name("Naruto")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/naruto")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .lastChapter(700.0)
                                                .build());

                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .mangas(mangas)
                                .dateTime(LocalDateTime.now())
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                List<MangaModel> result = listOfMangasWhitNewChapterService
                                .getLastListNewManga(ScrappersEnum.leerCapitulo);

                assertEquals(mangas, result);
                assertEquals(2, result.size());
                assertEquals("One Piece", result.get(0).getName());
                assertEquals("Naruto", result.get(1).getName());
        }

        @Test
        @DisplayName("getLastListNewManga - Debe retornar lista vacía cuando el modelo existe pero getMangas() retorna lista vacía")
        void testGetLastListNewMangaWhenModelExistsWithEmptyMangas() {
                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .mangas(List.of())
                                .dateTime(LocalDateTime.now())
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                List<MangaModel> result = listOfMangasWhitNewChapterService
                                .getLastListNewManga(ScrappersEnum.leerCapitulo);

                assertEquals(List.of(), result);
                assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getLastListNewManga - Debe retornar null cuando el modelo existe pero getMangas() retorna null")
        void testGetLastListNewMangaWhenModelExistsButMangasIsNull() {
                ListOfMangasWhitNewChapterModel model = ListOfMangasWhitNewChapterModel.builder()
                                .scraper(ScrappersEnum.leerCapitulo)
                                .mangas(null)
                                .dateTime(LocalDateTime.now())
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(model));

                List<MangaModel> result = listOfMangasWhitNewChapterService
                                .getLastListNewManga(ScrappersEnum.leerCapitulo);

                assertNull(result);
        }

        // Test save
        @SuppressWarnings({ "null" })
        @Test
        @DisplayName("save - No debe hacer nada cuando scrappersEnum es null")
        void testSaveWithNullScrappersEnum() {
                List<MangaModel> mangas = List.of(
                                MangaModel.builder()
                                                .id("1")
                                                .name("One Piece")
                                                .build());

                listOfMangasWhitNewChapterService.save(mangas, null);

                verify(newListMangaRepository, never()).findByScraper(any());
                verify(newListMangaRepository, never()).save(any(ListOfMangasWhitNewChapterModel.class));
        }

        @SuppressWarnings({ "null" })
        @Test
        @DisplayName("save - Debe convertir mangas null a List.of() y guardar cuando el modelo no existe")
        void testSaveWithNullMangasWhenModelNotExists() {
                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.empty());

                listOfMangasWhitNewChapterService.save(null, ScrappersEnum.leerCapitulo);

                verify(newListMangaRepository).save(argThat(model -> model.getMangas() != null &&
                                model.getMangas().isEmpty() &&
                                model.getScraper() == ScrappersEnum.leerCapitulo &&
                                model.getDateTime() != null));
        }

        @SuppressWarnings({ "null" })
        @Test
        @DisplayName("save - Debe actualizar el modelo existente cuando ya existe en la base de datos")
        void testSaveUpdatesExistingModel() {

                List<MangaModel> existingMangas = List.of(
                                MangaModel.builder()
                                                .id("1")
                                                .name("Old Manga")
                                                .build());

                List<MangaModel> newMangas = List.of(
                                MangaModel.builder()
                                                .id("2")
                                                .name("New Manga")
                                                .build());

                LocalDateTime oldDateTime = LocalDateTime.now().minusHours(2);
                ListOfMangasWhitNewChapterModel existingModel = ListOfMangasWhitNewChapterModel.builder()
                                .id("existing-id")
                                .scraper(ScrappersEnum.leerCapitulo)
                                .mangas(existingMangas)
                                .dateTime(oldDateTime)
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(existingModel));

                LocalDateTime beforeSave = LocalDateTime.now();
                listOfMangasWhitNewChapterService.save(newMangas, ScrappersEnum.leerCapitulo);
                LocalDateTime afterSave = LocalDateTime.now();

                verify(newListMangaRepository).findByScraper(ScrappersEnum.leerCapitulo);

                ArgumentCaptor<ListOfMangasWhitNewChapterModel> captor = ArgumentCaptor
                                .forClass(ListOfMangasWhitNewChapterModel.class);
                verify(newListMangaRepository).save(captor.capture());

                ListOfMangasWhitNewChapterModel savedModel = captor.getValue();
                assertNotNull(savedModel);
                assertEquals("existing-id", savedModel.getId());
                assertEquals(newMangas, savedModel.getMangas());
                assertEquals(ScrappersEnum.leerCapitulo, savedModel.getScraper());
                assertNotNull(savedModel.getDateTime());
                // Verificar que la fecha esté entre beforeSave y afterSave
                assertTrue(!savedModel.getDateTime().isBefore(beforeSave.minusSeconds(1)));
                assertTrue(!savedModel.getDateTime().isAfter(afterSave.plusSeconds(1)));
        }

        @Test
        @DisplayName("save - Debe crear un nuevo modelo cuando no existe en la base de datos")
        void testSaveCreatesNewModelWhenNotExists() {
                List<MangaModel> mangas = List.of(
                                MangaModel.builder()
                                                .id("1")
                                                .name("One Piece")
                                                .url(UrlModel.builder()
                                                                .url("https://example.com/one-piece")
                                                                .scrapper(ScrappersEnum.leerCapitulo)
                                                                .build())
                                                .lastChapter(1100.0)
                                                .build());

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.empty());

                listOfMangasWhitNewChapterService.save(mangas, ScrappersEnum.leerCapitulo);

                verify(newListMangaRepository).save(argThat(model -> model.getMangas().equals(mangas) &&
                                model.getScraper() == ScrappersEnum.leerCapitulo &&
                                model.getDateTime() != null));
        }

        @SuppressWarnings({ "null" })
        @Test
        @DisplayName("save - Debe actualizar el modelo existente con lista vacía cuando mangas es null")
        void testSaveUpdatesExistingModelWithEmptyListWhenMangasIsNull() {
                ListOfMangasWhitNewChapterModel existingModel = ListOfMangasWhitNewChapterModel.builder()
                                .id("existing-id")
                                .scraper(ScrappersEnum.leerCapitulo)
                                .mangas(List.of(
                                                MangaModel.builder()
                                                                .id("1")
                                                                .name("Old Manga")
                                                                .build()))
                                .dateTime(LocalDateTime.now().minusHours(1))
                                .build();

                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.of(existingModel));

                listOfMangasWhitNewChapterService.save(null, ScrappersEnum.leerCapitulo);

                verify(newListMangaRepository).save(argThat(model -> model.getId().equals("existing-id") &&
                                model.getMangas() != null &&
                                model.getMangas().isEmpty() &&
                                model.getScraper() == ScrappersEnum.leerCapitulo &&
                                model.getDateTime() != null));
        }

        @SuppressWarnings({ "null" })
        @Test
        @DisplayName("save - Debe crear nuevo modelo cuando no existe y mangas es null")
        void testSaveCreatesNewModelWhenNotExistsAndMangasIsNull() {
                when(newListMangaRepository.findByScraper(any())).thenReturn(Optional.empty());

                listOfMangasWhitNewChapterService.save(null, ScrappersEnum.leerCapitulo);

                verify(newListMangaRepository).save(argThat(model -> model.getMangas() != null &&
                                model.getMangas().isEmpty() &&
                                model.getScraper() == ScrappersEnum.leerCapitulo &&
                                model.getDateTime() != null));
        }
}
