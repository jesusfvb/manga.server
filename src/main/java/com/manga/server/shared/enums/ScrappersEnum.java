package com.manga.server.shared.enums;

public enum ScrappersEnum {
    leerCapitulo("https://www.leercapitulo.re");
   

    private String url;

    ScrappersEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
