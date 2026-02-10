package com.manga.server.features.chapter.controller;

import java.util.List;

import com.manga.server.features.chapter.controller.pageable.ChapterPageable;
import com.manga.server.features.chapter.controller.querty.ChapterQuery;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.chapter.responses.ChapterResponse;
import com.manga.server.features.chapter.user_cases.ChapterService;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.images.mappers.ImgMapper;
import com.manga.server.features.images.user_cases.ImgService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
@AllArgsConstructor
public class ChapterControllerV1 {

    final ChapterService chapterService;
    final ChapterMapper chapterMapper;

    final ImgService imgService;
    final ImgMapper imgMapper;

    @GetMapping("/mangas/{mangaId}/chapters")
    List<ChapterResponse> getChapters(
            @PathVariable String mangaId,
            @ParameterObject @ModelAttribute ChapterQuery query,
            @ParameterObject ChapterPageable pageable
    ) {
        return chapterMapper.chaptersToChapterDTOs(chapterService.getChapters(mangaId));
    }

}
