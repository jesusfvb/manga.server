package com.manga.server.features.manga.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.manga.dtos.MangaDTO;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.services.MangaService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mangas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MangaControllerV1 {

  final MangaService mangaService;
  final MangaMapper mangaMapper;

  @GetMapping()
  public ResponseEntity<List<MangaDTO>> getMangas(@ParameterObject @ModelAttribute MangaQuery query) {
    List<MangaModel> mangas = mangaService.mangasWithNewChapters();
    return ResponseEntity.ok((mangaMapper.mangasToMangaDTOs(mangas)));
  }

}
