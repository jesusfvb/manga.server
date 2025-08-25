package com.manga.server.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.dtos.ImgDTO;
import com.manga.server.models.ImgModel;

@Mapper(componentModel = "spring")
public interface ImgMapper {
    public ImgDTO imgModelToImgDTO(ImgModel img);

    public List<ImgDTO> imgModelsToImgDTOs(List<ImgModel> imgList);
}
