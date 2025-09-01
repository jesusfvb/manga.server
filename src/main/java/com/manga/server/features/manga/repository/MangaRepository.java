package com.manga.server.features.manga.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.manga.model.MangaModel;

@Repository
public interface MangaRepository extends MongoRepository<MangaModel, String> {

}
