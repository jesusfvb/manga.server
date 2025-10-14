package com.manga.server.features.manga.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "manga")
public class MangaModel {

  @Id
  private String id;

  @Indexed(unique = true)
  private String name;

  private String url;

  private String thumbnail;

  private String description;

  private LocalDateTime lastUpdated;

  private Double lastChapter;
}
