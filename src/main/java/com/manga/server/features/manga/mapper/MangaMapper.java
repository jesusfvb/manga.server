package com.manga.server.features.manga.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.features.manga.dtos.MangaDTO;
import com.manga.server.features.manga.dtos.NewMangaDTO;
import com.manga.server.features.manga.dtos.SearchMangaDTO;
import com.manga.server.features.manga.model.MangaModel;

@Mapper(componentModel = "spring")
public interface MangaMapper {

  List<MangaDTO> mangasToMangaDTOs(List<MangaModel> mangas);

  MangaDTO mangaToMangaDTO(MangaModel manga);

  List<NewMangaDTO> mangasToNewMangaDTOs(List<MangaModel> mangas);

  NewMangaDTO mangaToNewMangaDTO(MangaModel manga);

  SearchMangaDTO mangaToSearchMangaDTO(MangaModel manga);

  List<SearchMangaDTO> mangasToSearchMangaDTOs(List<MangaModel> mangas);
}
