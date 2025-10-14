package com.manga.server.features.manga.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manga.server.features.scrapper.enums.ScrappersEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "list_of_mangas_whit_new_chapter")
public class ListOfMangasWhitNewChapterModel {

    @Id
    private String id;

    private ScrappersEnum scraper;

    private LocalDateTime dateTime;

    private List<MangaModel> mangas;
}
