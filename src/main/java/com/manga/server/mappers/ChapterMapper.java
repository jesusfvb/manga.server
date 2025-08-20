package com.manga.server.mappers;

import com.manga.server.dtos.ChapterDTO;
import com.manga.server.models.ChapterModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    
    ChapterDTO chapterToChapterDTO(ChapterModel chapter);

    List<ChapterDTO> chaptersToChapterDTOs(List<ChapterModel> chapters);
}
