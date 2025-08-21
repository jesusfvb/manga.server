package com.manga.server.controllers;

import com.manga.server.dtos.ChapterDTO;
import com.manga.server.mappers.ChapterMapper;
import com.manga.server.services.ChapterService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter")
@CrossOrigin("*")
@AllArgsConstructor
public class ChapterController {
  final ChapterService chapterService;
  final ChapterMapper chapterMapper;

  @GetMapping
  List<ChapterDTO> getChapters(@RequestParam String mangaId) {
    return chapterMapper.chaptersToChapterDTOs(chapterService.getChapters(mangaId));
  }
}
