package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.ImgModel;
import com.manga.server.repository.ImgRepository;
import org.apache.commons.lang3.function.Consumers;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class ImgService {
    final ScrapperService scrapperService;
    final ImgRepository repository;
    final ChapterService chapterService;

    public List<ImgModel> getImg(String chapterId) {
        var example = Example.of(ImgModel.builder().chapterId(chapterId).build());
        var imgs = repository.findAll(example);
        if (imgs.isEmpty()) {
            var chapter = chapterService.getChapterById(chapterId);
            imgs = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapter.getUrl());
            imgs.forEach(img -> img.setChapterId(chapterId));
            repository.saveAll(imgs);
            imgs = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapter.getUrl());
        }
        imgs.sort(Comparator.comparingInt(ImgModel::getNumber));
        return imgs;
    }
}
