package com.manga.server.features.images.user_cases;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.user_cases.ChapterService;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.images.repocitory.ImgRepository;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

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

        var imgs = repository.findByChapterIdOrderByNumberAsc(chapterId);

        if (imgs == null || imgs.isEmpty()) {
            var chapter = chapterService.getChapterById(chapterId);
            if (chapter == null) {
                log.warn("No se encontró el capítulo con ID: {}", chapterId);
                return List.of();
            }

            String chapterUrl = chapter.getUrl().getFullUrl();
            if (chapterUrl == null || chapterUrl.isEmpty()) {
                log.warn("El capítulo con ID {} no tiene URL", chapterId);
                return List.of();
            }

            var scrapedImgs = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapterUrl);
            if (scrapedImgs != null && !scrapedImgs.isEmpty()) {
                scrapedImgs.forEach(img -> {
                    if (img != null) {
                        img.setChapterId(chapterId);
                        if (img.getLastUpdated() == null) {
                            img.setLastUpdated(LocalDateTime.now());
                        }
                    }
                });
                imgs = repository.saveAll(scrapedImgs);
                // Ordenar después de guardar ya que saveAll no garantiza orden
                if (imgs != null && !imgs.isEmpty()) {
                    imgs.sort(Comparator.comparingInt(ImgModel::getNumber));
                }
            } else {
                log.warn("No se pudieron obtener imágenes del capítulo con ID: {}", chapterId);
                return List.of();
            }
        }
        return imgs != null ? imgs : List.of();
    }

    public void preloadImages(List<String> chapterIds) {
        imgPreloadService.preloadImages(chapterIds);
    }
}
