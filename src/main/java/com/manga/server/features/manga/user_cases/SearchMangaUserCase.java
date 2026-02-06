package com.manga.server.features.manga.user_cases;

import com.manga.server.features.manga.comparator.MangaComparator;
import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.scrapper.services.ScrapperService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class SearchMangaUserCase {

    private final ScrapperService scrapperService;
    private final SaveMangasUseCase saveMangasUseCase;

    public Page<MangaModel> execute(String search, List<MangaModel> mangas, Pageable pageable) {

        List<MangaModel> scraped = scrapperService.searchManga(search);

        if (scraped == null || scraped.isEmpty()) {
            return new PageImpl<>(mangas, pageable, mangas.size());
        }

        saveMangasUseCase.execute(scraped);

        Map<String, MangaModel> map = new LinkedHashMap<>();

        for (MangaModel manga : mangas) {
            if (manga.getUrl() != null) {
                map.put(manga.getUrl().getUrl(), manga);
            }
        }

        for (MangaModel manga : scraped) {
            if (manga.getUrl() != null) {
                map.put(manga.getUrl().getUrl(), manga);
            }
        }
        List<MangaModel> combined = new ArrayList<>(map.values());

        combined.sort(MangaComparator.of(pageable));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combined.size());

        List<MangaModel> pageContent = combined.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                combined.size()
        );

    }

}
