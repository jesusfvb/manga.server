package com.manga.server.features.chapter.controller;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.chapter.controller.pageable.ChapterPageable;
import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.chapter.responses.ChapterPageResponse;
import com.manga.server.features.chapter.user_cases.GetChapterUserCase;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
@AllArgsConstructor
public class ChapterControllerV1 {

    final GetChapterUserCase getChapterUserCase;
    final ChapterMapper chapterMapper;


    @GetMapping("/mangas/{mangaId}/chapters")
        public ResponseEntity<ChapterPageResponse> getChapters(
            @PathVariable String mangaId,
            @ParameterObject @ModelAttribute ChapterQuery query,
            @ParameterObject ChapterPageable pageable) {

        var chapters = getChapterUserCase.execute(mangaId, query, pageable.toPageable());
        var chapterResponses = chapters.map(chapterMapper::toChapterResponse);

        return ResponseEntity.ok(new ChapterPageResponse(
            chapterResponses.getContent(),
            chapterResponses.getNumber(),
            chapterResponses.getSize(),
            chapterResponses.getTotalElements(),
            chapterResponses.getTotalPages(),
            chapterResponses.isLast(),
            chapterResponses.isFirst()
        ));
    }

}
