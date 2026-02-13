package com.manga.server.features.images.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.features.images.controller.querty.ImageQuery;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageControllerV1 {

    @GetMapping("chapter/{chapterId}/images")
    ResponseEntity<Void> getImages(@PathVariable String chapterId, @ParameterObject @ModelAttribute ImageQuery query) {
        return ResponseEntity.ok().build();
    }


}
