package com.manga.server.features.chapter.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.dtos.ChapterDTO;
import com.manga.server.features.chapter.models.ChapterModel;

@Service
@Mapper(componentModel = "spring")
public interface ChapterMapper {

    ChapterDTO chapterToChapterDTO(ChapterModel chapter);

    List<ChapterDTO> chaptersToChapterDTOs(List<ChapterModel> chapters);
}
