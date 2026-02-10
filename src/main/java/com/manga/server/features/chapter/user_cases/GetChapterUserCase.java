package com.manga.server.features.chapter.user_cases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetChapterUserCase {

    private final ChapterRepository chapterRepository;

    public Page<ChapterModel> execute(String mangaId, Pageable pageable) {
        return chapterRepository.findByMangaIdOrderByNumber(mangaId, pageable);
    }

}
