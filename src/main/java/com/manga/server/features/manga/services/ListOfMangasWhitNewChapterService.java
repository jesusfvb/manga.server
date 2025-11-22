package com.manga.server.features.manga.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.model.ListOfMangasWhitNewChapterModel;
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
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        // scrappersEnum ya está validado arriba, el builder es seguro
        @SuppressWarnings("null")
        var newListManga = newListMangaRepository.findOne(Example.of(example));
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
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        // scrappersEnum ya está validado arriba, el builder es seguro
        @SuppressWarnings("null")
        var newListManga = newListMangaRepository.findOne(Example.of(example));
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
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        // scrappersEnum ya está validado arriba, el builder es seguro
        @SuppressWarnings("null")
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if(newListManga.isPresent()){
            var existingModel = newListManga.get();
            if (existingModel != null) {
                existingModel.setMangas(mangas);
                existingModel.setDateTime(LocalDateTime.now());
                newListMangaRepository.save(existingModel);
            }
        }
        else {
            example.setDateTime(LocalDateTime.now());
            example.setMangas(mangas);
            newListMangaRepository.save(example);
        }
    }

    private boolean isMoreThanMinutes(LocalDateTime date) {
        int MINUTES = 30;

        Duration duration = Duration.between(date,LocalDateTime.now());
        Duration fiveMinutes = Duration.ofMinutes(MINUTES);
        return duration.compareTo(fiveMinutes) > 0;
    }

}
