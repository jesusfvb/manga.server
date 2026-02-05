package com.manga.server.features.manga.user_cases;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class SaveMangasUseCase {

    private final MangaRepository mangaRepository;

    public void execute(List<MangaModel> mangas) {

        if (mangas == null || mangas.isEmpty()) {
            return;
        }

        for (MangaModel manga : mangas) {

            if (!isValid(manga)) continue;

            normalize(manga);

            Optional<MangaModel> existing =
                    mangaRepository.findByNameIgnoreCaseAndUrl(
                            manga.getName(),
                            manga.getUrl()
                    );

            if (existing.isEmpty()) {
                MangaModel saved = mangaRepository.save(manga);
                manga.setId(saved.getId());
            } else {
                manga.setId(existing.get().getId());
            }
        }
    }

    private boolean isValid(MangaModel manga) {
        return manga != null
                && manga.getName() != null
                && manga.getUrl() != null;
    }

    private void normalize(MangaModel manga) {
        if (manga.getLastChapter() == null) {
            manga.setLastChapter(0.0);
        }
    }
}

