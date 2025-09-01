package com.manga.server.features.manga.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "manga")
public class MangaModel {

  @Id
  private String id;

  @Indexed(unique = true)
  private String name;

  private String url;

  private String thumbnail;

  private Double numberOfChapters;

  private String description;
}
