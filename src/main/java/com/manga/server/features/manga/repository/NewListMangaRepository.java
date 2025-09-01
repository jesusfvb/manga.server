package com.manga.server.features.manga.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.manga.model.NewListMangaModel;

@Repository
public interface NewListMangaRepository extends MongoRepository<NewListMangaModel,String> {
}
