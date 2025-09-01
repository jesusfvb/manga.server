package com.manga.server.features.chapter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.chapter.models.ImgModel;

@Repository
public interface ImgRepository extends MongoRepository<ImgModel,String>{

    
} 