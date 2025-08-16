package com.manga.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.models.MangaModel;

@Repository
public interface MangaRepository extends MongoRepository<MangaModel, String> {

}
