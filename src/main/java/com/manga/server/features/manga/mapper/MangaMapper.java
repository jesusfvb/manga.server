package com.manga.server.features.manga.mapper;

import java.util.List;

import com.manga.server.features.manga.dtos.MangaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.manga.server.features.manga.model.MangaModel;

@Mapper(componentModel = "spring")
public interface MangaMapper {

  List<MangaResponse> mangasToMangaDTOs(List<MangaModel> mangas);

  @Mapping(target = "thumbnail", source = "manga.thumbnail.fullUrl")
  MangaResponse mangaToMangaDTO(MangaModel manga);

}
