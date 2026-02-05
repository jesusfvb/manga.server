package com.manga.server.features.manga.controller.query;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MangaQuery {
    String search;
    List<String> ids;
    MangaFilter filter;
}
