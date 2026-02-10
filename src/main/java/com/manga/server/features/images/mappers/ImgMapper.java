package com.manga.server.features.images.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import com.manga.server.features.images.responces.ImgDTO;
import com.manga.server.features.images.models.ImgModel;

@Service
@Mapper(componentModel = "spring")
public interface ImgMapper {

    @Mapping(target = "url", source = "img.url.url")
    public ImgDTO imgModelToImgDTO(ImgModel img);

    public List<ImgDTO> imgModelsToImgDTOs(List<ImgModel> imgList);
}
