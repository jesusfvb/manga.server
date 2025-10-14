package com.manga.server;

import com.manga.server.features.manga.services.MangaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Autowired
  private MangaService mangaService;

    @Override
    public void run(String... args) throws Exception {
        mangaService.starApp();
    }
}
