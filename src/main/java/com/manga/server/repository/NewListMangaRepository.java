package com.manga.server.repository;

import com.manga.server.models.NewListMangaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewListMangaRepository extends MongoRepository<NewListMangaModel,String> {
}
