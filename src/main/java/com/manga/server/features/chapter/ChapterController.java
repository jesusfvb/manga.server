package com.manga.server.features.chapter;

import com.manga.server.features.chapter.dtos.ChapterDTO;
import com.manga.server.features.chapter.dtos.ImgDTO;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.chapter.mappers.ImgMapper;
import com.manga.server.features.chapter.services.ChapterService;
import com.manga.server.features.chapter.services.ImgService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter")
@CrossOrigin("*")
@AllArgsConstructor
public class ChapterController {

  final ChapterService chapterService;
  final ChapterMapper chapterMapper;

  final ImgService imgService;
  final ImgMapper imgMapper;

  @GetMapping
  List<ChapterDTO> getChapters(@RequestParam String mangaId) {
    return chapterMapper.chaptersToChapterDTOs(chapterService.getChapters(mangaId));
  }

  @GetMapping("/img")
  public List<ImgDTO> getImg(@RequestParam String chapterId) {
    var images = imgService.getImg(chapterId);
    return imgMapper.imgModelsToImgDTOs(images);
  }

  @GetMapping("/img/preload")
  public ResponseEntity<Void> preloadImages(@RequestParam List<String> chapterIds) {
    imgService.preloadImages(chapterIds);
    return ResponseEntity.ok().build();
  }
}
