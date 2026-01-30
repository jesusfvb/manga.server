package com.manga.server.features.chapter.controller.image;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class ImagePreloadRequest {
    @NotEmpty
    private List<String> chapterIds;
}
