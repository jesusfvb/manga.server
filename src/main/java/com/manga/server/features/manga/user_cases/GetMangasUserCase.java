package com.manga.server.features.manga.user_cases;

import com.manga.server.features.manga.controller.query.MangaQuery;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.getmangas.GetMangaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetMangasUserCase {

    private final GetMangaRepository mangaRepository;
    private final SearchMangaUserCase searchMangaUserCase;

    public Page<MangaModel> execute(MangaQuery query, Pageable pageable) {

        var mangas = mangaRepository.findAll(query, pageable);

        if(validSearch(query.getSearch() )&& sizeLestThat(mangas) ) {
            return searchMangaUserCase.execute(query.getSearch(), mangas.getContent(), pageable);
        }

        return mangas;
    }


    private boolean validSearch(String search) {
        return search != null && !search.trim().isEmpty();
    }

    private boolean sizeLestThat(Page<MangaModel> mangas) {
        if (mangas == null) return false;
        mangas.getContent();
        return mangas.getContent().isEmpty() ||  mangas.getTotalElements() <= 5;
    }
}
