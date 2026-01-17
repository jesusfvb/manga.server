package com.manga.server.features.chapter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.chapter.models.ImgModel;

@Repository
public interface ImgRepository extends MongoRepository<ImgModel, String> {

    List<ImgModel> findByChapterIdOrderByNumberAsc(String chapterId);

    List<ImgModel> findByChapterId(String chapterId);

    List<ImgModel> findByChapterIdAndLastUpdatedBefore(String chapterId, LocalDateTime dateTime);
}