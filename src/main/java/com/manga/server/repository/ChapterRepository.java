package com.manga.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.models.ChapterModel;

@Repository
public interface ChapterRepository extends MongoRepository<ChapterModel,String> {
    
}
