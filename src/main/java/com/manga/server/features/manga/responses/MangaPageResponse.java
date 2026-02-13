package com.manga.server.features.manga.responses;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.manga.server.core.page.PageBase;

@Getter
@NoArgsConstructor
@Schema(name = "MangaPage")
public class MangaPageResponse extends PageBase {

    private List<MangaResponse> content;

    public MangaPageResponse(
            List<MangaResponse> content,
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
