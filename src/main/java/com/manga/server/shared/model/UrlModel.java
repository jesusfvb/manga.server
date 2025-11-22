package com.manga.server.shared.model;

import com.manga.server.shared.enums.ScrappersEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlModel {
    private String url;
    private ScrappersEnum scrapper;

    public String getFullUrl() {
        return scrapper.getUrl() + url;
    }
}
