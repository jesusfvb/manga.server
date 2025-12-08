package com.manga.server.core.browser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

/**
 * Implementación del wrapper de Jsoup que delega a Jsoup real.
 */
@Component
@Log
public class JsoupWrapper {

    public Document getDocument(String url) throws IOException {
        log.fine("JsoupWrapper obteniendo documento de: " + url);
        return Jsoup.connect(url).get();
    }
}
