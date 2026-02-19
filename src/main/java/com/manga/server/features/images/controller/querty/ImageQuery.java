package com.manga.server.features.images.controller.querty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageQuery {

    @Schema(description = "Obtener todas las imágenes sin paginación", defaultValue = "false")
    private Boolean unpaged = false;
}
