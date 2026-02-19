package com.manga.server.features.images.user_cases;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.repository.ChapterRepository;
import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncImagesUseCase {

    private final ChapterRepository chapterRepository;
    private final ScrapperService scrapperService;
    private final SaveImagesUseCase saveImagesUseCase;

    public List<ImgModel> execute(String chapterId) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("SyncImagesUseCase ejecutado con chapterId nulo o vacío");
            return List.of();
        }

        log.info("Sincronizando imágenes con scraper para chapterId: {}", chapterId);

        Optional<ChapterModel> chapterOpt = chapterRepository.findById(chapterId);
        if (chapterOpt.isEmpty()) {
            log.warn("No se encontró el capítulo con ID: {}", chapterId);
            return List.of();
        }

        ChapterModel chapter = chapterOpt.get();
        String chapterUrl = chapter.getUrl().getFullUrl();

        if (chapterUrl == null || chapterUrl.isEmpty()) {
            log.warn("El capítulo con ID {} no tiene URL", chapterId);
            return List.of();
        }

        List<ImgModel> scrapedImages = scrapeImages(chapterUrl, chapterId);

        if (scrapedImages.isEmpty()) {
            return List.of();
        }

        return saveImagesUseCase.execute(scrapedImages, chapterId);
    }

    private List<ImgModel> scrapeImages(String chapterUrl, String chapterId) {
        log.info("Scrapeando imágenes desde URL: {}", chapterUrl);
        List<ImgModel> scrapedImages = scrapperService.getImg(ScrappersEnum.leerCapitulo, chapterUrl);

        if (scrapedImages == null || scrapedImages.isEmpty()) {
            log.warn("No se pudieron obtener imágenes del scraper para chapterId: {}", chapterId);
            return List.of();
        }

        log.info("Se scrapearon {} imágenes para chapterId: {}", scrapedImages.size(), chapterId);
        return scrapedImages;
    }
}

