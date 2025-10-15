package com.manga.server.features.manga.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.model.ListOfMangasWhitNewChapterModel;
import com.manga.server.features.manga.repository.ListOfMangasWhitNewChapterRepository;
import com.manga.server.features.scrapper.enums.ScrappersEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ListOfMangasWhitNewChapterService {

    final ListOfMangasWhitNewChapterRepository newListMangaRepository;

    Boolean isTimeCheck(ScrappersEnum scrappersEnum) {
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if (newListManga.isPresent()) {
            return isMoreThanMinutes(newListManga.get().getDateTime());
        }
        return true;
    }

    List<MangaModel> getLastListNewManga(ScrappersEnum scrappersEnum) {
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if (newListManga.isPresent()) return newListManga.get().getMangas();
        return null;
    }

    void save(List<MangaModel> mangas, ScrappersEnum scrappersEnum) {
        var example = ListOfMangasWhitNewChapterModel.builder().scraper(scrappersEnum).build();
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if(newListManga.isPresent()){
            newListManga.get().setMangas(mangas);
            newListManga.get().setDateTime(LocalDateTime.now());
            newListMangaRepository.save(newListManga.get());
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
