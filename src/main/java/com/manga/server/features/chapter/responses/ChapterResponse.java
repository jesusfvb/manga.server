package com.manga.server.features.chapter.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Chapter")
public record ChapterResponse(String id, Double number) {
}
