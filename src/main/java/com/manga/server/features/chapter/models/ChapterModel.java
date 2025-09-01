package com.manga.server.features.chapter.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("chapters")
public class ChapterModel {
    @Id
    String id;

    Double number;

    String[] images;

    String url;

    String mangaId;
}
