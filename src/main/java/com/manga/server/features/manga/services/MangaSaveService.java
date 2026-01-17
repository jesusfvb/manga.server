package com.manga.server.features.manga.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MangaSaveService {

    private final MangaRepository mangaRepository;

    public void saveIfNotExists(MangaModel manga) {
        if (manga == null) {
            return;
        }
        if (manga.getName() == null || manga.getUrl() == null) {
            return;
        }

        if (manga.getLastChapter() == null) {
            manga.setLastChapter(0.0);
        }

        Optional<MangaModel> existingManga = mangaRepository.findByNameIgnoreCaseAndUrl(
                manga.getName(),
                manga.getUrl());

        if (existingManga.isEmpty()) {
            var savedManga = mangaRepository.save(manga);
            if (savedManga != null && savedManga.getId() != null) {
                manga.setId(savedManga.getId());
            }
        } else {
            var existing = existingManga.get();
            if (existing != null && existing.getId() != null) {
                manga.setId(existing.getId());
            }
        }
    }

    public void saveIfNotExists(List<MangaModel> listManga) {
        if (listManga == null || listManga.isEmpty()) {
            return;
        }
        for (var manga : listManga) {
            saveIfNotExists(manga);
        }
    }
}
