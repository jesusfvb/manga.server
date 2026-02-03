package com.manga.server.features.manga.controller;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;

@Setter
@Getter
public class MangaQuery {
    String search;
    List<String> ids;
    MangaFilter filter;
}
