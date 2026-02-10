package com.manga.server.features.chapter.user_cases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SyncChapterUserCase {

    final ScrapperService scrapperService;
    final MangaRepository mangaRepository;
    final SaveChapterUserCase saveChapterUserCase;

    public List<ChapterModel> execute(String mangaId) {
        var manga = mangaRepository.findById(mangaId).orElseThrow(() -> new RuntimeException("Manga not found"));
        var chapters = scrapperService.getChapters(manga.getUrl());
        saveChapterUserCase.execute(mangaId, chapters);
        return chapters;
    }

}
