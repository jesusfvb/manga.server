package com.manga.server.features.manga.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.shared.model.UrlModel;

@Repository
public interface MangaRepository extends MongoRepository<MangaModel, String> {

    List<MangaModel> findByNameContainingIgnoreCase(String name);

    Optional<MangaModel> findByNameIgnoreCase(String name);

    Optional<MangaModel> findByNameIgnoreCaseAndUrl(String name, UrlModel url);
}
