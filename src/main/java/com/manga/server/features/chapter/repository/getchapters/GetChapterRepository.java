package com.manga.server.features.chapter.repository.getchapters;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import com.manga.server.features.chapter.models.ChapterModel;

@Service
public interface GetChapterRepository {
    Page<ChapterModel> findAll(String mangaId, ChapterQuery query, Pageable pageable);
}
