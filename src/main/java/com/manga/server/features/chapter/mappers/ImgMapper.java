package com.manga.server.features.chapter.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.features.chapter.dtos.ImgDTO;
import com.manga.server.features.chapter.models.ImgModel;

@Mapper(componentModel = "spring")
public interface ImgMapper {
    public ImgDTO imgModelToImgDTO(ImgModel img);

    public List<ImgDTO> imgModelsToImgDTOs(List<ImgModel> imgList);
}
