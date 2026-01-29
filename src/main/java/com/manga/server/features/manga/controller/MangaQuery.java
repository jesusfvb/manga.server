package com.manga.server.features.manga.controller;

import java.util.List;

import lombok.Getter;

@Getter
public class MangaQuery {
    String search;
    List<String> ids;
    MangaFilter filter = MangaFilter.ALL;
}
