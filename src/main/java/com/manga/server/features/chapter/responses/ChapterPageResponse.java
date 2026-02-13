package com.manga.server.features.chapter.responses;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.manga.server.core.page.PageBase;

@Getter
@NoArgsConstructor
@Schema(name = "ChapterPage")
public class ChapterPageResponse extends PageBase {

    private List<ChapterResponse> content;

    public ChapterPageResponse(
            List<ChapterResponse> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean last,
            boolean first) {
        super(pageNumber, pageSize, totalElements, totalPages, last, first);
        this.content = content;
    }
}
