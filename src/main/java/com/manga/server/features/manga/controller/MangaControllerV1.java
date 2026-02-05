package com.manga.server.features.manga.controller;

import com.manga.server.core.page.PageResponse;
import com.manga.server.features.manga.controller.pageable.MangaPageable;
import com.manga.server.features.manga.controller.query.MangaQuery;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.responses.MangaResponse;
import com.manga.server.features.manga.user_cases.GetMangasUserCase;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mangas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MangaControllerV1 {

    private final GetMangasUserCase getMangasUserCase;
    final MangaMapper mangaMapper;

    @GetMapping()
    public ResponseEntity<PageResponse<MangaResponse>> getMangas(
            @ParameterObject @ModelAttribute MangaQuery query,
            @ParameterObject  MangaPageable pageable
    ) {
        var mangasPage = getMangasUserCase.execute(query, pageable.toPageable());
        return ResponseEntity.ok().body(PageResponse.<MangaResponse>builder()
                .content(mangasPage.getContent().stream().map(mangaMapper::mangaToMangaResponse).toList())
                .pageNumber(mangasPage.getNumber())
                .pageSize(mangasPage.getSize())
                .totalElements(mangasPage.getTotalElements())
                .totalPages(mangasPage.getTotalPages())
                .build());
    }

}
