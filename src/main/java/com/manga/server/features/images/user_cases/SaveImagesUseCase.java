package com.manga.server.features.images.user_cases;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.manga.server.features.images.models.ImgModel;
import com.manga.server.features.images.repocitory.ImgRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveImagesUseCase {

    private final ImgRepository repository;

    public List<ImgModel> execute(List<ImgModel> images, String chapterId) {
        if (images == null || images.isEmpty()) {
            log.warn("SaveImagesUseCase ejecutado con lista vacía");
            return List.of();
        }

        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("SaveImagesUseCase ejecutado con chapterId nulo o vacío");
            return List.of();
        }

        log.info("Preparando {} imágenes para guardar en BD para chapterId: {}", images.size(), chapterId);

        prepareImagesForSave(images, chapterId);
        List<ImgModel> savedImages = repository.saveAll(images);

        log.info("Se guardaron {} imágenes en BD para chapterId: {}", savedImages.size(), chapterId);
        return savedImages;
    }

    private void prepareImagesForSave(List<ImgModel> images, String chapterId) {
        images.forEach(img -> {
            if (img != null) {
                img.setChapterId(chapterId);
                if (img.getLastUpdated() == null) {
                    img.setLastUpdated(LocalDateTime.now());
                }
            }
        });
    }
}

