package com.manga.server.features.manga.services;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MangaSaveService {

    private final MangaRepository mangaRepository;

    /**
     * Guarda un manga si no existe, o actualiza su ID si ya existe.
     * 
     * @param manga El manga a guardar o actualizar
     */
    public void saveIfNotExists(MangaModel manga) {
        if (manga == null) {
            return;
        }
        var example = MangaModel.builder()
                .name(manga.getName() != null ? manga.getName() : "")
                .url(manga.getUrl())
                .build();
        // El builder es seguro, los campos null son manejados correctamente por Spring
        // Data
        @SuppressWarnings("null")
        var exit = mangaRepository.findOne(Example.of(example));
        if (exit.isEmpty()) {
            var savedManga = mangaRepository.save(manga);
            if (savedManga != null && savedManga.getId() != null) {
                manga.setId(savedManga.getId());
            }
        } else {
            var existingManga = exit.get();
            if (existingManga != null && existingManga.getId() != null) {
                manga.setId(existingManga.getId());
            }
        }
    }

    /**
     * Guarda una lista de mangas si no existen, o actualiza sus IDs si ya existen.
     * 
     * @param listManga La lista de mangas a guardar o actualizar
     */
    public void saveIfNotExists(List<MangaModel> listManga) {
        if (listManga == null || listManga.isEmpty()) {
            return;
        }
        for (var manga : listManga) {
            saveIfNotExists(manga);
        }
    }
}
