package com.manga.server.features.chapter.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import com.manga.server.features.chapter.models.ChapterModel;
import com.manga.server.features.chapter.responses.ChapterResponse;

@Service
@Mapper(componentModel = "spring")
public interface ChapterMapper {
    ChapterResponse toChapterResponse(ChapterModel chapter);
}
