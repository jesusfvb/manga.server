package com.manga.server.features.manga.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Manga")
public record MangaResponse(String id, String name, String thumbnail, Integer lastChapter, String description) {
};
