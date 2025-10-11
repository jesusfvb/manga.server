package com.manga.server.features.manga.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "manga")
public class MangaModel {

        @Id
        private String id;

        @Indexed(unique = true)
        private String name;

        private String url;

        private String thumbnail;

        private String description;

        private LocalDateTime lastUpdated;

        @Transient
        @Builder.Default
        private Double lastChapter = null;
}
