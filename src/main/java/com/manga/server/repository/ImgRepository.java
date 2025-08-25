package com.manga.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.models.ImgModel;

@Repository
public interface ImgRepository extends MongoRepository<ImgModel,String>{

    
} 