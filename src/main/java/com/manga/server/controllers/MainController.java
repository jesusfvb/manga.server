package com.manga.server.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.dtos.NewMangaDTO;
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
  ResponseEntity<List<NewMangaDTO>> getNewMangas() {

    List<MangaModel> mangas = leercapituloScraper.getNewChapter();
    List<NewMangaDTO> newMangaDTOList = mangaMapper.mangasToNewMangaDTOs(mangas);

    return ResponseEntity.ok(newMangaDTOList);
  }

}
