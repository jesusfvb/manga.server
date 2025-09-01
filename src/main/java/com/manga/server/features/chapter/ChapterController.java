package com.manga.server.features.chapter;

import com.manga.server.features.chapter.dtos.ChapterDTO;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.chapter.services.ChapterService;

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
