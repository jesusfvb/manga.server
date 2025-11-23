package com.manga.server.features.chapter.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manga.server.shared.enums.ScrappersEnum;
import com.manga.server.shared.model.UrlModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "images")
public class ImgModel {

    @Id
    private String id;

    private UrlModel url;

    private ScrappersEnum scrapper;

    private Integer number;

    private String chapterId;

    private LocalDateTime lastUpdated;
}
