package com.manga.server.features.manga.user_cases;

import com.manga.server.features.scrapper.services.ScrapperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SyncMangasUseCase {

    private ScrapperService scrapperService;
    private SaveMangasUseCase saveMangasUseCase;

    public void execute() {
    var mangasWhitNewChapter = scrapperService.getMangasWithNewChapters();
    saveMangasUseCase.execute(mangasWhitNewChapter,true);
    }
}
