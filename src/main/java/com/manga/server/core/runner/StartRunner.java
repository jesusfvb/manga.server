package com.manga.server.core.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.manga.server.features.manga.services.MangaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Component
@RequiredArgsConstructor
@Log
public class StartRunner implements CommandLineRunner {

    private final MangaService mangaService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando aplicación - ejecutando starApp()");
        mangaService.starApp();
    }
}

