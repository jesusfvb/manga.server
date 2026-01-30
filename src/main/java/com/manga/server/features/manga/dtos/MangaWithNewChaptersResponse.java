package com.manga.server.features.manga.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MangaWithNewChapters")
public record MangaWithNewChaptersResponse(String id, String name, String thumbnail, double numberOfChapters) {
}
