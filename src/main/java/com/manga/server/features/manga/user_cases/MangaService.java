package com.manga.server.features.manga.user_cases;

import java.util.List;

import com.manga.server.features.manga.controller.MangaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.manga.server.features.manga.model.MangaModel;
import com.manga.server.features.manga.repository.MangaRepository;
import com.manga.server.features.scrapper.services.ScrapperService;
import com.manga.server.shared.enums.ScrappersEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Service
@RequiredArgsConstructor
@Log
public class MangaService {

    private final ScrapperService scrapperService;
    private final MangaRepository mangaRepository;
    private final ListOfMangasWhitNewChapterService listOfMangasWhitNewChapterService;
    private final MangaSaveService mangaSaveService;
    private final MangaUpdateService mangaUpdateService;

    public void starApp() {
        mangaUpdateService.updateMangasWithNewChapters();
    }

    public List<MangaModel> getMangasByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return mangaRepository.findAllById(ids);
    }

    public MangaModel getMangaById(String mangaId) {
        if (mangaId == null || mangaId.isEmpty()) {
            log.warning("getMangaById llamado con mangaId nulo o vacío");
            return null;
        }
        return mangaRepository.findById(mangaId).orElse(null);
    }

    public List<MangaModel> mangasWithNewChapters() {
        mangaUpdateService.updateMangasWithNewChapters();
        var scrapper = ScrappersEnum.leerCapitulo;
        var mangasWhitNewChapters = listOfMangasWhitNewChapterService.getLastListNewManga(scrapper);
        return mangasWhitNewChapters != null ? mangasWhitNewChapters : List.of();
    }

    public List<MangaModel> searchManga(String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }

        var mangas = mangaRepository.findByNameContainingIgnoreCase(query);
        var logMessage = "Is search for: data base";

        // TODO Actualizar este numero en un futuro
        if (mangas.size() <= 3) {
            var mangasScrapper = scrapperService.searchManga(ScrappersEnum.leerCapitulo, query);
            if (mangasScrapper != null) {
                for (var magaScraper : mangasScrapper) {
                    if (magaScraper != null && mangas.stream()
                            .noneMatch(m -> m != null && m.getName() != null && m.getName().equals(magaScraper.getName()))) {
                        mangaSaveService.saveIfNotExists(magaScraper);
                        mangas.add(magaScraper);
                        logMessage = "Is search for:" + scrapperService.toString();
                    }
                }
            }
        }
        log.info(logMessage);
        return mangas;
    }

    public Page<MangaModel> getMangas(MangaQuery query, Pageable pageable) {
       if (query == null) { return  mangaRepository.findAll(pageable); }

         return null;
    }
}
