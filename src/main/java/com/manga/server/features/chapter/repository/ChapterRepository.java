package com.manga.server.features.chapter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.chapter.models.ChapterModel;

@Repository
public interface ChapterRepository extends MongoRepository<ChapterModel, String> {

    List<ChapterModel> findByMangaIdOrderByNumberAsc(String mangaId);

    List<ChapterModel> findByMangaId(String mangaId);

    List<ChapterModel> findByMangaIdAndLastUpdatedBefore(String mangaId, LocalDateTime dateTime);
}
