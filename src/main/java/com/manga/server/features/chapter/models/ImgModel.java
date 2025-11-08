package com.manga.server.features.chapter.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "images")
public class ImgModel {

    @Id
    public String id;

    public String url;

    public Integer number;

    public String chapterId;

    public LocalDateTime lastUpdated;
}
