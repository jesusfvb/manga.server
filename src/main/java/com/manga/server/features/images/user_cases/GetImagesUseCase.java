package com.manga.server.features.images.user_cases;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.images.comparator.ImageComparator;
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

    public Page<ImgModel> execute(String chapterId, Pageable pageable) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("GetImagesUseCase ejecutado con chapterId nulo o vacío");
            return Page.empty(pageable);
        }

        log.info("Obteniendo imágenes paginadas para chapterId: {}", chapterId);

        Page<ImgModel> images = repository.findByChapterId(chapterId, pageable);

        if (images.isEmpty()) {
            log.info("No se encontraron imágenes en BD, sincronizando con scraper");
            var syncImages = syncImagesUseCase.execute(chapterId);

            syncImages.sort(ImageComparator.of(pageable));

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), syncImages.size());
            var pageContent = start >= end ? List.<ImgModel>of() : syncImages.subList(start, end);

            return new PageImpl<>(pageContent, pageable, syncImages.size());
        }

        return images;
    }
}
