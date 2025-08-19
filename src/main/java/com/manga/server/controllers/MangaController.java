package com.manga.server.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.dtos.DescriptionDTO;
import com.manga.server.dtos.NewMangaDTO;
import com.manga.server.dtos.SearchMangaDTO;
import com.manga.server.mappers.MangaMapper;
import com.manga.server.models.MangaModel;
import com.manga.server.services.MangaService;

import lombok.RequiredArgsConstructor;

@RestController("/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MangaController {

    final MangaService mangaService;
    final MangaMapper mangaMapper;

    @GetMapping
    public ResponseEntity<List<NewMangaDTO>> getNewMangas() {

        List<MangaModel> mangas = mangaService.newMangas();
        List<NewMangaDTO> newMangaDTOList = mangaMapper.mangasToNewMangaDTOs(mangas);

        return ResponseEntity.ok(newMangaDTOList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchMangaDTO>> searchMangas(@RequestParam String query) {
        List<MangaModel> mangas = mangaService.searchManga(query);
        return ResponseEntity.ok((mangaMapper.mangasToSearchMangaDTOs(mangas)));
    }


    @GetMapping("/description")
    public ResponseEntity<DescriptionDTO> mangaDescription(@RequestParam String id) {
        return ResponseEntity.ok(new DescriptionDTO(mangaService.mangaDescription(id)));
    }

}
