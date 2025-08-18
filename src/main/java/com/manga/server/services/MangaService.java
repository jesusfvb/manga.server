package com.manga.server.services;

import com.manga.server.enums.ScrappersEnum;
import com.manga.server.models.MangaModel;
import com.manga.server.repository.MangaRepository;
import com.manga.server.scrapers.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class MangaService {

    final Scraper leerCapituloScraper;
    final MangaRepository mangaRepository;
    final NewListMangaService newListMangaService;

    public List<MangaModel> newMangas() {
        var scrapper = ScrappersEnum.leerCapitulo;
        if (newListMangaService.isTimeCheck(scrapper)) {
            var mangas = leerCapituloScraper.getNewChapter();
            exitOfSave(mangas);
            newListMangaService.saveListNewMangas(mangas,scrapper);
            log.info("Is for:" + scrapper);
            return mangas;
        } else {
            log.info("Is for: data base" );
            return newListMangaService.getLastListNewManga(scrapper);
        }
    }

    public List<MangaModel> searchManga(String query) {
        var mangas = leerCapituloScraper.searchMangas(query);
        exitOfSave(mangas);
        return mangas;
    }

    public String mangaDescription(String id){
        var mangaOptional = mangaRepository.findById(id);
        if(mangaOptional.isPresent()){
         var manga = mangaOptional.get();
         if(manga.getDescription()== null){
           var description =  leerCapituloScraper.getMangaDescription(manga.getUrl());
           manga.setDescription(description);
           mangaRepository.save(manga);
           return  description;
         }
         return  manga.getDescription();
        }
        return  null;
    }

    private void exitOfSave(List<MangaModel> listManga) {
        for (var manga : listManga) {
            var exmaple = MangaModel.builder().name(manga.getName()).url(manga.getUrl()).build();
            var exit = mangaRepository.findOne(Example.of(exmaple));
            if (exit.isEmpty()) {
                var id = mangaRepository.save(manga).getId();
                manga.setId(id);
            } else {
                manga.setId(exit.get().getId());
            }
        }
    }
}
