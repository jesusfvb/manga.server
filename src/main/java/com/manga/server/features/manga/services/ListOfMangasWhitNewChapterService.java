package com.manga.server.features.manga.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.ListOfMangasWhitNewChapterModel;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.ListOfMangasWhitNewChapterRepository;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ListOfMangasWhitNewChapterService {

    final ListOfMangasWhitNewChapterRepository newListMangaRepository;

    Boolean isTimeCheck(ScrappersEnum scrappersEnum) {
        if (scrappersEnum == null) {
            return true;
        }
        Optional<ListOfMangasWhitNewChapterModel> newListManga = newListMangaRepository.findByScraper(scrappersEnum);
        if (newListManga.isPresent()) {
            var listModel = newListManga.get();
            if (listModel != null && listModel.getDateTime() != null) {
                return isMoreThanMinutes(listModel.getDateTime());
            }
        }
        return true;
    }

    List<MangaModel> getLastListNewManga(ScrappersEnum scrappersEnum) {
        if (scrappersEnum == null) {
            return null;
        }
        Optional<ListOfMangasWhitNewChapterModel> newListManga = newListMangaRepository.findByScraper(scrappersEnum);
        if (newListManga.isPresent()) {
            var listModel = newListManga.get();
            if (listModel != null) {
                return listModel.getMangas();
            }
        }
        return null;
    }

    void save(List<MangaModel> mangas, ScrappersEnum scrappersEnum) {
        if (scrappersEnum == null) {
            return;
        }
        if (mangas == null) {
            mangas = List.of();
        }

        Optional<ListOfMangasWhitNewChapterModel> existingList = newListMangaRepository.findByScraper(scrappersEnum);

        ListOfMangasWhitNewChapterModel listModel;
        if (existingList.isPresent()) {
            var existing = existingList.get();
            if (existing != null) {
                existing.setDateTime(LocalDateTime.now());
                existing.setMangas(mangas);
                listModel = existing;
            } else {
                listModel = ListOfMangasWhitNewChapterModel.builder()
                        .scraper(scrappersEnum)
                        .dateTime(LocalDateTime.now())
                        .mangas(mangas)
                        .build();
            }
        } else {
            listModel = ListOfMangasWhitNewChapterModel.builder()
                    .scraper(scrappersEnum)
                    .dateTime(LocalDateTime.now())
                    .mangas(mangas)
                    .build();
        }

        newListMangaRepository.save(listModel);
    }

    private boolean isMoreThanMinutes(LocalDateTime date) {
        int MINUTES = 30;

        Duration duration = Duration.between(date, LocalDateTime.now());
        return duration.toMinutes() > MINUTES;
    }

}
