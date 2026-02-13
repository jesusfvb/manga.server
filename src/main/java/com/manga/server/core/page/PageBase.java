package com.manga.server.core.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class PageBase {

    @Schema(description = "Page number")
    private int pageNumber;

    @Schema(description = "Page size")
    private int pageSize;

    @Schema(description = "Total elements")
    private long totalElements;

    @Schema(description = "Total pages")
    private int totalPages;

    @Schema(description = "Is last page")
    private boolean last;

    @Schema(description = "Is first page")
    private boolean first;

    protected PageBase(
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean last,
            boolean first) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
    }
}
