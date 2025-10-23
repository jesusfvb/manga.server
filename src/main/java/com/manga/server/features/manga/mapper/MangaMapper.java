package com.manga.server.features.manga.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.features.manga.dtos.MangaDTO;
import com.manga.server.features.manga.model.MangaModel;

@Mapper(componentModel = "spring")
public interface MangaMapper {

  List<MangaDTO> mangasToMangaDTOs(List<MangaModel> mangas);

  MangaDTO mangaToMangaDTO(MangaModel manga);

}
