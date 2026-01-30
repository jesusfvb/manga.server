package com.manga.server.features.manga.controller;

import com.manga.server.core.page.PageResponse;
import com.manga.server.features.manga.dtos.MangaResponse;
import com.manga.server.features.manga.dtos.MangaWithNewChaptersResponse;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.services.MangaService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mangas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MangaControllerV1 {

    final MangaService mangaService;
    final MangaMapper mangaMapper;

    @GetMapping()
    public ResponseEntity<PageResponse<MangaResponse>> getMangas(
            @ParameterObject @ModelAttribute MangaQuery query,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var mangas = mangaService.getMangas(query, pageable);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/new-chapters")
    public ResponseEntity<PageResponse<MangaWithNewChaptersResponse>> getMangasWhitNewChapters(
            Pageable pageable
    ) {
        return ResponseEntity.ok(null);
    }

}
