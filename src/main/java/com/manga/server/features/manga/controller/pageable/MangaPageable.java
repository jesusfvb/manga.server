package com.manga.server.features.manga.controller.pageable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class MangaPageable {
    @Schema(defaultValue = "0")
    private  Integer page = 0;

    @Schema(defaultValue = "10",maximum = "50",minimum = "0")
    private  Integer size = 10;

    @Schema(defaultValue = "TITLE")
    private MangaSortField sortField =MangaSortField.TITLE;

    @Schema(defaultValue = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;

    @JsonIgnore
    private static final int MAX_SIZE = 50;

    public Pageable toPageable() {
        int safePage = page != null && page >= 0 ? page : 0;
        int safeSize = size != null && size > 0
                ? Math.min(size, MAX_SIZE)
                : 10;

        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by(direction, sortField.name())
        );}
}