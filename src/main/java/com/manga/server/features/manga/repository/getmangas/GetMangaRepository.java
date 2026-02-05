package com.manga.server.features.manga.repository.getmangas;

import com.manga.server.features.manga.controller.query.MangaQuery;
import com.manga.server.features.manga.model.MangaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface GetMangaRepository {
    Page<MangaModel> findAll(MangaQuery query, Pageable pageable);
}