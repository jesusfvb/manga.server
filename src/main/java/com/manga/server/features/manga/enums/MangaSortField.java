package com.manga.server.features.manga.enums;

import lombok.Getter;

@Getter
public enum MangaSortField {
    LAST_UPDATED("lastUpdated"),
    TITLE("name");


    private final String fieldName;

    MangaSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static   MangaSortField fromFieldName(String fieldName) {
        for (MangaSortField sortField : values()) {
            if (sortField.getFieldName().equalsIgnoreCase(fieldName)) {
                return sortField;
            }
        }
        throw new IllegalArgumentException("Campo de ordenamiento no válido: " + fieldName);
    }

}
