package com.manga.server.features.chapter.user_cases;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.comparator.ChapterComparator;
import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.getchapters.GetChapterRepositoryImpl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetChapterUserCase {

    private final GetChapterRepositoryImpl chapterRepository;
    private final SyncChapterUserCase syncChapterUserCase;

    public Page<ChapterModel> execute(String mangaId, ChapterQuery query, Pageable pageable) {
        var chapters = chapterRepository.findAll(mangaId, query, pageable);
        if (chapters.isEmpty()) {
            var syncChapters = syncChapterUserCase.execute(mangaId);
        
            syncChapters.sort(ChapterComparator.of(pageable));

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), syncChapters.size());
            var pageContent = start >= end ? List.<ChapterModel>of() : syncChapters.subList(start, end);

            return new PageImpl<>(pageContent, pageable, syncChapters.size());

        }
        return chapters;
    }

}
