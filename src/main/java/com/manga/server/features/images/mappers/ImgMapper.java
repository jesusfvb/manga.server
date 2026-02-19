package com.manga.server.features.images.mappers;

import java.util.List;

import com.manga.server.features.images.responses.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.manga.server.features.images.models.ImgModel;

@Mapper(componentModel = "spring")
public interface ImgMapper {

    @Mapping(target = "url", source = "url.url")
    ImageResponse imgModelToImageResponse(ImgModel img);

    List<ImageResponse> imgModelsToImgDTOs(List<ImgModel> imgList);
}
