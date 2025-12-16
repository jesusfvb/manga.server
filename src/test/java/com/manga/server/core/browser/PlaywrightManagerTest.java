package com.manga.server.core.browser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.manga.server.shared.enums.ScrappersEnum;
import com.microsoft.playwright.ElementHandle;

/**
 * Tests para el método querySelectorAll de PlaywrightManager.
 * Estos tests verifican el comportamiento del método con diferentes escenarios.
 */
@ExtendWith(MockitoExtension.class)
public class PlaywrightManagerTest {

    @InjectMocks
    private PlaywrightManager playwrightManager;

    @Test
    @DisplayName("querySelectorAll - Debe retornar lista de elementos cuando se proporciona una URL, selector y script")
    void testquerySelectorAll() throws Exception {
        // Given
        String url = "https://www.google.com";
        String selector = "textarea";
        List<ElementHandle> elements = playwrightManager.querySelectorAll(url, selector, null, (element) -> element);
        assertNotNull(elements);
        assertEquals(2, elements.size());
    }

    @Test
    @DisplayName("querySelectorAll - Debe retornar lista de elementos cuando se proporciona una URL, selector y script")
    void testquerySelectorAllWithScript() {

        var scrapper = ScrappersEnum.leerCapitulo.getUrl();
        // Given
        String url = scrapper + "/leer/8375z8arvm/resurreccion-solitaria/1/";
        String selector = ".comic_wraCon > a";
        String script = "localStorage.setItem('display_mode', '1')";

        Function<ElementHandle, ElementHandle> mapper = (element) -> element;
        try {
            List<ElementHandle> elements = playwrightManager.querySelectorAll(url, selector, script, mapper);
            assertNotNull(elements);
            assertEquals(80, elements.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error al obtener los elementos");
        }
    }
}
