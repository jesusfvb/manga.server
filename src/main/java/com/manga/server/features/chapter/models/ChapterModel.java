package com.manga.server.features.chapter.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("chapters")
public class ChapterModel {
  @Id
  private String id;

  private Double number;

  private String[] images;

  private String url;

  private String mangaId;

  private LocalDateTime lastUpdated;
}
