package com.manga.server.features.chapter.mappers;

import com.manga.server.features.chapter.dtos.ChapterDTO;
import com.manga.server.features.chapter.models.ChapterModel;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    
    ChapterDTO chapterToChapterDTO(ChapterModel chapter);

    List<ChapterDTO> chaptersToChapterDTOs(List<ChapterModel> chapters);
}
