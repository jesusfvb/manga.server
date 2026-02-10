package com.manga.server.features.chapter.controller;

import com.manga.server.features.chapter.controller.querty.ImageQuery;
import com.manga.server.features.chapter.requests.ImagePreloadRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageControllerV1 {

    @GetMapping("chapter/{chapterId}/images")
    ResponseEntity<Void> getImages(@PathVariable String chapterId, @ParameterObject @ModelAttribute ImageQuery query) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("images/preload")
    public ResponseEntity<Void> postMethodName(@RequestBody ImagePreloadRequest request) {
        return ResponseEntity.accepted().build();
    }

}
