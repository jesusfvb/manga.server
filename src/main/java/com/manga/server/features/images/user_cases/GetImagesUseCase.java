package com.manga.server.features.images.user_cases;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.images.comparator.ImageComparator;
import com.manga.server.features.images.controller.querty.ImageQuery;
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

    public Page<ImgModel> execute(String chapterId, ImageQuery query, Pageable pageable) {
        if (chapterId == null || chapterId.isEmpty()) {
            log.warn("GetImagesUseCase ejecutado con chapterId nulo o vacío");
            return Page.empty(pageable);
        }

        boolean isUnpaged = Boolean.TRUE.equals(query.getUnpaged());
        log.info("Obteniendo imágenes {} para chapterId: {}",
            isUnpaged ? "SIN PAGINAR" : "paginadas", chapterId);

        // Cuando unpaged=true, usar Pageable.unpaged() pero conservar el Sort del pageable original
        Pageable queryPageable = isUnpaged
            ? Pageable.unpaged(pageable.getSort())
            : pageable;

        Page<ImgModel> images = repository.findByChapterId(chapterId, queryPageable);

        if (images.isEmpty()) {
            log.info("No se encontraron imágenes en BD, sincronizando con scraper");
            List<ImgModel> syncedImages = syncImagesUseCase.execute(chapterId);
            return buildPageFromList(syncedImages, queryPageable, isUnpaged);
        }

        return images;
    }

    private Page<ImgModel> buildPageFromList(List<ImgModel> images, Pageable pageable, boolean unpaged) {
        if (images.isEmpty()) {
            return Page.empty(pageable);
        }

        images.sort(ImageComparator.of(pageable));

        if (unpaged) {
            return new PageImpl<>(images, Pageable.unpaged(pageable.getSort()), images.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), images.size());
        List<ImgModel> pageContent = start >= end ? List.of() : images.subList(start, end);

        return new PageImpl<>(pageContent, pageable, images.size());
    }
}
