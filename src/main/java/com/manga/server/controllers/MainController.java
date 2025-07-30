package com.manga.server.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manga.server.scrapers.Scraper;

import lombok.RequiredArgsConstructor;

@RestController("/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class MainController {

  final Scraper leercapituloScraper;

  @GetMapping
  String getMain() {
    return leercapituloScraper.getNewChapter();
  }

}
