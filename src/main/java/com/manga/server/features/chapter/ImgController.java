package com.manga.server.features.chapter;

import com.manga.server.features.chapter.dtos.ImgDTO;
import com.manga.server.features.chapter.mappers.ImgMapper;
import com.manga.server.features.chapter.services.ImgService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/img")
public class ImgController {
    final ImgService imgService;
    final ImgMapper imgMapper;

    @GetMapping()
    public List<ImgDTO> getImg(String chapterId){
        var images = imgService.getImg(chapterId);
        return imgMapper.imgModelsToImgDTOs(images);
    }
}
