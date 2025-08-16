package com.manga.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Document(collection = "manga")
public class MangaModel {

  @Id
  final String id;

  @Indexed(unique = true)
  final String name;

  final String url;

  final String thumbnail;

  final Double numberOfChapters;

}
