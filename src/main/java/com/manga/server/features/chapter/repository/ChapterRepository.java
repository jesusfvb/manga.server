package com.manga.server.features.chapter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.chapter.models.ChapterModel;

@Repository
public interface ChapterRepository extends MongoRepository<ChapterModel,String> {
    
}
