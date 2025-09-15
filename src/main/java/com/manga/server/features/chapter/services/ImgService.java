package com.manga.server.features.chapter.services;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.chapter.repository.ImgRepository;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Service
@AllArgsConstructor
@Log
public class ImgService {
    final private ScrapperService scrapperService;
    final private ImgRepository repository;
    final private ChapterService chapterService;

    public List<ImgModel> getImg(String chapterId) {
        var example = Example.of(ImgModel.builder().chapterId(chapterId).build());
        var imgs = repository.findAll(example);
        if (imgs.isEmpty()) {
            var chapter = chapterService.getChapterById(chapterId);
            imgs = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapter.getUrl());
            imgs.forEach(img -> img.setChapterId(chapterId));
            repository.saveAll(imgs);
        }
        imgs.sort(Comparator.comparingInt(ImgModel::getNumber));
        return imgs;
    }


    @Async
    public void preLoadImages(List<ChapterModel> chapters) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<Void>> tasks = chapters.stream()
                .map(chapter -> CompletableFuture.runAsync(() -> {
                    getImg(chapter.getId());
                    log.info("Preloaded images for chapter " + chapter.getNumber());
                },executor))
                .toList();
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
    }
}
