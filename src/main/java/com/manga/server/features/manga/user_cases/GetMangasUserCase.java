package com.manga.server.features.manga.user_cases;

import com.manga.server.features.manga.controller.MangaQuery;
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

    public Page<MangaModel> execute(MangaQuery query, Pageable pageable) {
        return mangaRepository.findAll(query,pageable);
    }
}
