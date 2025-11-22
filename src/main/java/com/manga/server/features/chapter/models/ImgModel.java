package com.manga.server.features.chapter.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manga.server.shared.model.UrlModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "images")
public class ImgModel {

    @Id
    public String id;

    public UrlModel url;

    public Integer number;

    public String chapterId;

    public LocalDateTime lastUpdated;
}
