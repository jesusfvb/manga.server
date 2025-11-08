package com.manga.server.features.chapter.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ImgModel;
import com.manga.server.features.chapter.repository.ImgRepository;
import com.manga.server.features.scrapper.enums.ScrappersEnum;
import com.manga.server.features.scrapper.services.ScrapperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgService {

    private final ScrapperService scrapperService;
    private final ImgRepository repository;
    private final ChapterService chapterService;
    
    @Lazy
    @Autowired
    private ImgPreloadService imgPreloadService;


    public List<ImgModel> getImg(String chapterId) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("getImg llamado con chapterId nulo o vacío");
            return List.of();
        }

        var example = Example.of(ImgModel.builder().chapterId(chapterId).build());
        var imgs = repository.findAll(example);

        if (imgs.isEmpty()) {
            var chapter = chapterService.getChapterById(chapterId);
            if (chapter == null) {
                log.warn("No se encontró el capítulo con ID: {}", chapterId);
                return List.of();
            }

            var scrapedImgs = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapter.getUrl());
            if (scrapedImgs != null && !scrapedImgs.isEmpty()) {
                scrapedImgs.forEach(img -> {
                    img.setChapterId(chapterId);
                    if (img.getLastUpdated() == null) {
                        img.setLastUpdated(LocalDateTime.now());
                    }
                });
                imgs = repository.saveAll(scrapedImgs);
            } else {
                log.warn("No se pudieron obtener imágenes del capítulo con ID: {}", chapterId);
                return List.of();
            }
        }

        if (imgs != null && !imgs.isEmpty()) {
            imgs.sort(Comparator.comparingInt(ImgModel::getNumber));
        }
        return imgs != null ? imgs : List.of();
    }

    /**
     * Precarga imágenes de los capítulos especificados de forma asíncrona.
     * 
     * @param chapterIds Lista de IDs de capítulos a precargar
     */
    public void preloadImages(List<String> chapterIds) {
        imgPreloadService.preloadImages(chapterIds);
    }
}
