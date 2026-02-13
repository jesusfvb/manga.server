package com.manga.server.features.manga.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.manga.controller.pageable.MangaPageable;
import com.manga.server.features.manga.controller.query.MangaQuery;
import com.manga.server.features.manga.mapper.MangaMapper;
import com.manga.server.features.manga.responses.MangaPageResponse;
import com.manga.server.features.manga.user_cases.GetMangasUserCase;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mangas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MangaControllerV1 {

    private final GetMangasUserCase getMangasUserCase;
    private final MangaMapper mangaMapper;

    @GetMapping()
     
        public ResponseEntity<MangaPageResponse> getMangas(
            @ParameterObject @ModelAttribute MangaQuery query,
            @ParameterObject MangaPageable pageable) {
        var mangasPage = getMangasUserCase.execute(query, pageable.toPageable());
        var mangaResponses = mangasPage.map(mangaMapper::mangaToMangaResponse);

        return ResponseEntity.ok(new MangaPageResponse(
            mangaResponses.getContent(),
            mangaResponses.getNumber(),
            mangaResponses.getSize(),
            mangaResponses.getTotalElements(),
            mangaResponses.getTotalPages(),
            mangaResponses.isLast(),
            mangaResponses.isFirst()
        ));
    }

}
