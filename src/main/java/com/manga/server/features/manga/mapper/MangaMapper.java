package com.manga.server.features.manga.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.responses.MangaResponse;

@Mapper(componentModel = "spring")
public interface MangaMapper {
    @Mapping(target = "thumbnail", source = "manga.thumbnail.fullUrl")
    MangaResponse mangaToMangaResponse(MangaModel manga);
}
