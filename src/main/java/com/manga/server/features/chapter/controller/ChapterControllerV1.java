package com.manga.server.features.chapter.controller;

import com.manga.server.core.page.PageResponse;
import com.manga.server.features.chapter.controller.pageable.ChapterPageable;
import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.chapter.responses.ChapterResponse;
import com.manga.server.features.chapter.user_cases.GetChapterUserCase;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
@AllArgsConstructor
public class ChapterControllerV1 {

    final GetChapterUserCase getChapterUserCase;
    final ChapterMapper chapterMapper;

    @GetMapping("/mangas/{mangaId}/chapters")
    ResponseEntity<PageResponse<ChapterResponse>> getChapters(
            @PathVariable String mangaId,
            @ParameterObject @ModelAttribute ChapterQuery query,
            @ParameterObject ChapterPageable pageable) {

        var chapters = getChapterUserCase.execute(mangaId, pageable.toPageable());

        return ResponseEntity.ok(
                PageResponse.<ChapterResponse>builder()
                        .content(chapters.get().map(chapterMapper::toChapterResponse).toList())
                        .pageNumber(chapters.getNumber())
                        .pageSize(chapters.getSize())
                        .totalElements(chapters.getTotalElements())
                        .totalPages(chapters.getTotalPages())
                        .first(chapters.isFirst())
                        .last(chapters.isLast())
                        .build());
    }

}
