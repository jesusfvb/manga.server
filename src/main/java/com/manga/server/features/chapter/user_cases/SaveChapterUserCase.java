package com.manga.server.features.chapter.user_cases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SaveChapterUserCase {

    final ChapterRepository chapterRepository;

    void execute(String mangaId, List<ChapterModel> chapters) {

        chapters.forEach(chapter -> {
            if (chapterRepository.existsByMangaIdAndNumber(mangaId, chapter.getNumber())) {
                return;
            } else {
                chapter.setMangaId(mangaId);
                chapterRepository.save(chapter);
            }
        });
    }
}
