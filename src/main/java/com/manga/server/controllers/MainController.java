package com.manga.server.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.dtos.MangaDTO;
import com.manga.server.mappers.MangaMapper;
import com.manga.server.models.MangaModel;
import com.manga.server.scrapers.Scraper;

import lombok.RequiredArgsConstructor;

@RestController("/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MainController {

  final Scraper leercapituloScraper;
  final MangaMapper mangaMapper;

  @GetMapping
  List<MangaDTO> getMain() {

    List<MangaModel> mangas = leercapituloScraper.getNewChapter();

    return mangaMapper.mangasToMangaDTOs(mangas);
  }

}
