package com.manga.server.core.browser;

import org.springframework.stereotype.Component;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;

@Component
@Log
public class PlaywrightManager {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;

    public PlaywrightManager() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
    }

    public BrowserContext getContext() {
        return context;
    }

    @PreDestroy
    public void shutdown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        log.info("Playwright cerrado correctamente 🚀");
    }
}
