package com.manga.server.features.chapter.controller.chapter;

import java.util.List;

import com.manga.server.features.chapter.controller.chapter.querty.ChapterQuery;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.chapter.requests.ChapterDTO;
import com.manga.server.features.chapter.mappers.ChapterMapper;
import com.manga.server.features.chapter.mappers.ImgMapper;
import com.manga.server.features.chapter.services.ChapterService;
import com.manga.server.features.chapter.services.ImgService;

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
  List<ChapterDTO> getChapters(@PathVariable String mangaId, @ParameterObject @ModelAttribute ChapterQuery query) {
    return chapterMapper.chaptersToChapterDTOs(chapterService.getChapters(mangaId));
  }

  // @GetMapping("/img")
  // public List<ImgDTO> getImg(@RequestParam String chapterId) {
  // var images = imgService.getImg(chapterId);
  // return imgMapper.imgModelsToImgDTOs(images);
  // }

  // @GetMapping("/img/preload")
  // public ResponseEntity<Void> preloadImages(@RequestParam List<String>
  // chapterIds) {
  // imgService.preloadImages(chapterIds);
  // return ResponseEntity.ok().build();
  // }
}
