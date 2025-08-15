package com.manga.server.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.manga.server.dtos.MangaDTO;
import com.manga.server.dtos.NewMangaDTO;
import com.manga.server.dtos.SearchMangaDTO;
import com.manga.server.models.MangaModel;

@Mapper(componentModel = "spring")
public interface MangaMapper {

  List<MangaDTO> mangasToMangaDTOs(List<MangaModel> mangas);

  MangaDTO mangaToMangaDTO(MangaModel manga);

  List<NewMangaDTO> mangasToNewMangaDTOs(List<MangaModel> mangas);

  NewMangaDTO mangaToNewMangaDTO(MangaModel manga);

  SearchMangaDTO mangaToSearchMangaDTO(MangaModel manga);

  List<SearchMangaDTO> mangasToSearchMangaDTOs(List<MangaModel> mangas);
}
