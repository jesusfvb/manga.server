package com.manga.server.features.chapter.controller.chapter;

import java.util.List;

import lombok.Getter;

@Getter
public class ChapterQuery {
    List<String> ids;
    // ChapterFilter filter = ChapterFilter.ALL;
}
