package com.manga.server.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@CrossOrigin("*")
public class MainController {

  @GetMapping
  String getMain() {
    return "Welcome to the Manga Server API. Use /api/v1/manga to access the manga resources.";
  }

}
