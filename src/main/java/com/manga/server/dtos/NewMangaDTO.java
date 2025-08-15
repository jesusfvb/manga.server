package com.manga.server.dtos;

import java.util.UUID;

public record NewMangaDTO(UUID id, String name, String thumbnail,double numberOfChapters) {
}
