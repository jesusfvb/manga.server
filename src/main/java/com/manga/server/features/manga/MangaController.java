package com.manga.server.features.manga;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.manga.dtos.MangaDTO;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.services.MangaService;

import lombok.RequiredArgsConstructor;

@RestController("/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MangaController {

  final MangaService mangaService;
  final MangaMapper mangaMapper;

  @GetMapping
  public ResponseEntity<List<MangaDTO>> getMangasWhitNewChapters() {
    List<MangaModel> mangas = mangaService.mangasWithNewChapters();
    return ResponseEntity.ok((mangaMapper.mangasToMangaDTOs(mangas)));
  }

  @GetMapping("/search")
  public ResponseEntity<List<MangaDTO>> searchMangas(@RequestParam String query) {
    List<MangaModel> mangas = mangaService.searchManga(query);
    return ResponseEntity.ok((mangaMapper.mangasToMangaDTOs(mangas)));
  }

  @GetMapping("/ids")
  public ResponseEntity<List<MangaDTO>> getMethodName(@RequestParam List<String> ids) {
    var listManga = mangaService.getMangasByIds(ids);
    return ResponseEntity.ok(mangaMapper.mangasToMangaDTOs(listManga));
  }

}
