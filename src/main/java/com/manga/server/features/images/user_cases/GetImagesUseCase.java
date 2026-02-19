package com.manga.server.features.images.user_cases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.images.repocitory.ImgRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetImagesUseCase {

    private final ImgRepository repository;
    private final SyncImagesUseCase syncImagesUseCase;

    public List<ImgModel> execute(String chapterId) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("GetImagesUseCase ejecutado con chapterId nulo o vacío");
            return List.of();
        }

        log.info("Obteniendo imágenes para chapterId: {}", chapterId);
        List<ImgModel> images = repository.findByChapterIdOrderByNumberAsc(chapterId);

        if (images == null || images.isEmpty()) {
            log.info("No se encontraron imágenes en BD, sincronizando con scraper");
            return syncImagesUseCase.execute(chapterId);
        }

        log.info("Se obtuvieron {} imágenes de BD para chapterId: {}", images.size(), chapterId);
        return images;
    }
}

