package com.manga.server.features.images.responses;

import java.util.List;

import com.manga.server.core.page.PageBase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ImagePage")
public class ImagePageResponse extends PageBase {

    private List<ImageResponse> content;

    public ImagePageResponse(
            List<ImageResponse> content,
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

