package com.manga.server.features.images.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.images.controller.pageable.ImagePageable;
import com.manga.server.features.images.controller.querty.ImageQuery;
import com.manga.server.features.images.mappers.ImgMapper;
import com.manga.server.features.images.responses.ImagePageResponse;
import com.manga.server.features.images.user_cases.GetImagesUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
@RequiredArgsConstructor
public class ImageControllerV1 {

    private final GetImagesUseCase getImagesUseCase;
    private final ImgMapper imgMapper;

    @GetMapping("chapter/{chapterId}/images")
    public ResponseEntity<ImagePageResponse> getImages(
            @PathVariable String chapterId,
            @ParameterObject @ModelAttribute ImageQuery query,
            @ParameterObject ImagePageable pageable) {

        var imagesPage = getImagesUseCase.execute(chapterId, pageable.toPageable());
        var imageResponses = imagesPage.map(imgMapper::imgModelToImageResponse);

        return ResponseEntity.ok(new ImagePageResponse(
            imageResponses.getContent(),
            imageResponses.getNumber(),
            imageResponses.getSize(),
            imageResponses.getTotalElements(),
            imageResponses.getTotalPages(),
            imageResponses.isLast(),
            imageResponses.isFirst()
        ));
    }
}
