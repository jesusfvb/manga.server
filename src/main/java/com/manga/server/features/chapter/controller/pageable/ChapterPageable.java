package com.manga.server.features.chapter.controller.pageable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.manga.server.features.manga.enums.MangaSortField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Clase que encapsula los parámetros de paginación y ordenamiento para Manga.
 * Proporciona una interfaz para configurar la búsqueda de mangas con propiedades
 * filtrables y ordenables a través del Swagger/OpenAPI.
 */
@Getter
@Setter
public class ChapterPageable {
    @Schema(defaultValue = "0")
    private  Integer page = 0;

    @Schema(defaultValue = "10",maximum = "50",minimum = "0")
    private  Integer size = 10;

    @Schema(defaultValue = "ASC")
    private Sort.Direction direction = Sort.Direction.ASC;

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
                Sort.by(direction)
        );}
}