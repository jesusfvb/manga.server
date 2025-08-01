package com.manga.server.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.dtos.MangaDTO;
import com.manga.server.models.MangaModel;

@Mapper(componentModel = "spring")
public interface MangaMapper {

  List<MangaDTO> mangasToMangaDTOs(List<MangaModel> mangas);

  MangaDTO mangaToMangaDTO(MangaModel manga);
}
