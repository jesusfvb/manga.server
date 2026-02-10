package com.manga.server.features.chapter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.getchapters.GetChapterRepository;

@Repository
public interface ChapterRepository extends MongoRepository<ChapterModel, String>, GetChapterRepository {

    Boolean existsByMangaIdAndNumber(String mangaId, Double number);

}
