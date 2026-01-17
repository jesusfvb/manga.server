package com.manga.server.features.manga.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manga.server.features.manga.model.ListOfMangasWhitNewChapterModel;
import com.manga.server.shared.enums.ScrappersEnum;

@Repository
public interface ListOfMangasWhitNewChapterRepository extends MongoRepository<ListOfMangasWhitNewChapterModel, String> {

    Optional<ListOfMangasWhitNewChapterModel> findByScraper(ScrappersEnum scraper);
}
