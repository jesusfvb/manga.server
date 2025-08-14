package com.manga.server.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Document(collection = "manga")
public class MangaModel {

  @Id
  final UUID id;

  final String name;

  final String url;

  final String thumbnail;

  final Double numberOfChapters;

}
