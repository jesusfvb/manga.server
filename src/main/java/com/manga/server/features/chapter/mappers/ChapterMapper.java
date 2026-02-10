package com.manga.server.features.chapter.mappers;

import java.util.List;

import com.manga.server.features.chapter.responses.ChapterResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;

@Service
@Mapper(componentModel = "spring")
public interface ChapterMapper {

    ChapterResponse chapterToChapterDTO(ChapterModel chapter);

    List<ChapterResponse> chaptersToChapterDTOs(List<ChapterModel> chapters);
}
