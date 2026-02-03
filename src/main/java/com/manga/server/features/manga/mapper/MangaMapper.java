package com.manga.server.features.manga.mapper;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.responses.MangaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public interface MangaMapper {
    @Mapping(target = "thumbnail", source = "manga.thumbnail.fullUrl")
    MangaResponse mangaToMangaResponse(MangaModel manga);
}
