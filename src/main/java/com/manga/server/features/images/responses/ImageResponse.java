package com.manga.server.features.images.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name="Image")
public record ImageResponse(String id, Integer number, String url) {
    
}
