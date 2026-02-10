package com.manga.server.features.chapter.controller.querty;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterQuery {
    List<String> ids;
    // ChapterFilter filter = ChapterFilter.ALL;
}
