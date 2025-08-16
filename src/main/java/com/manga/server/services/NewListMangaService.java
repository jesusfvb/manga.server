package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.MangaModel;
import com.manga.server.models.NewListMangaModel;
import com.manga.server.repository.MangaRepository;
import com.manga.server.repository.NewListMangaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class NewListMangaService {

    final NewListMangaRepository newListMangaRepository;
    private final MangaRepository mangaRepository;

    Boolean isTimeCheck(ScrappersEnum scrappersEnum) {
        var example = NewListMangaModel.builder().scraper(scrappersEnum).build();
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if (newListManga.isPresent()) {
            return isMoreThanMinutes(newListManga.get().getDateTime());
        }
        return true;
    }

    List<MangaModel> getLastListNewManga(ScrappersEnum scrappersEnum) {
        var example = NewListMangaModel.builder().scraper(scrappersEnum).build();
        var newListManga = newListMangaRepository.findOne(Example.of(example));
        if (newListManga.isPresent()) return newListManga.get().getMangas();
        return null;
    }

    void saveListNewMangas(List<MangaModel> mangas, ScrappersEnum scrappersEnum) {
        var example = NewListMangaModel.builder().scraper(scrappersEnum).build();
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
        int MINUTES = 5;

        Duration duration = Duration.between(date,LocalDateTime.now());
        Duration fiveMinutes = Duration.ofMinutes(MINUTES);
        return duration.compareTo(fiveMinutes) > 0;
    }

}
