package com.manga.server.features.images.user_cases;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ImgPreloadServiceTests {

    @InjectMocks
    private ImgPreloadService imgPreloadService;

    @Mock
    private ImgService imgService;

    private Executor testExecutor;

    @BeforeEach
    void setUp() {
        // Usar un executor síncrono para hacer los tests más predecibles
        // Esto ejecutará las tareas en el mismo hilo, facilitando las verificaciones
        testExecutor = Executors.newSingleThreadExecutor();
        
        // Inyectar el executor usando reflection ya que es final
        try {
            var field = ImgPreloadService.class.getDeclaredField("executor");
            field.setAccessible(true);
            field.set(imgPreloadService, testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Error al inyectar executor en tests", e);
        }
    }

    // Test preloadImages

    @Test
    @DisplayName("preloadImages - Debe manejar correctamente cuando chapterIds es null")
    void testPreloadImagesWithNullChapterIds() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        // Ejecutar en un hilo separado para no bloquear
        new Thread(() -> {
            imgPreloadService.preloadImages(null);
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(2, TimeUnit.SECONDS));
        Mockito.verify(imgService, Mockito.never()).getImg(ArgumentMatchers.any(String.class));
    }

    @Test
    @DisplayName("preloadImages - Debe filtrar IDs nulos o vacíos")
    void testPreloadImagesFiltersNullAndEmptyIds() throws InterruptedException {
        List<String> chapterIds = new ArrayList<>(Arrays.asList("valid-1", null, "", "valid-2", "  ", "valid-3"));
        
        // Usar lenient para permitir llamadas con diferentes argumentos
        Mockito.lenient().when(imgService.getImg("valid-1")).thenReturn(List.of());
        Mockito.lenient().when(imgService.getImg("valid-2")).thenReturn(List.of());
        Mockito.lenient().when(imgService.getImg("valid-3")).thenReturn(List.of());
        Mockito.lenient().when(imgService.getImg("  ")).thenReturn(List.of());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
            imgPreloadService.preloadImages(chapterIds);
            try {
                // Esperar a que se procese
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        
        // Verificar que se llamó con los IDs válidos (el código no filtra strings con solo espacios)
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("valid-1");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("valid-2");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("valid-3");
        Mockito.verify(imgService, Mockito.never()).getImg(null);
        Mockito.verify(imgService, Mockito.never()).getImg("");
    }

    @Test
    @DisplayName("preloadImages - Debe procesar capítulos cuando se proporcionan IDs válidos")
    void testPreloadImagesWithValidChapterIds() throws InterruptedException {
        List<String> chapterIds = List.of("chapter-1", "chapter-2");
        
        Mockito.when(imgService.getImg("chapter-1")).thenReturn(List.of());
        Mockito.when(imgService.getImg("chapter-2")).thenReturn(List.of());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
            imgPreloadService.preloadImages(chapterIds);
            try {
                // Esperar a que se procese
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        
        // Verificar que se llamó a getImg para cada capítulo
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-1");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-2");
    }

    @Test
    @DisplayName("preloadImages - Debe agregar capítulos a la cola cuando ya está ejecutándose")
    void testPreloadImagesAddsToQueueWhenAlreadyRunning() throws InterruptedException {
        List<String> firstBatch = List.of("chapter-1");
        List<String> secondBatch = List.of("chapter-2");
        
        Mockito.when(imgService.getImg("chapter-1")).thenAnswer(invocation -> {
            // Simular procesamiento lento
            Thread.sleep(500);
            return List.of();
        });
        Mockito.when(imgService.getImg("chapter-2")).thenReturn(List.of());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
            // Primera llamada
            imgPreloadService.preloadImages(firstBatch);
            
            // Segunda llamada mientras la primera está procesando
            try {
                Thread.sleep(50); // Pequeño delay para que la primera empiece
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            imgPreloadService.preloadImages(secondBatch);
            
            try {
                // Esperar a que se procese todo
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        
        // Ambos deberían procesarse eventualmente
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-1");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-2");
    }

    @Test
    @DisplayName("preloadImages - Debe manejar errores al procesar un capítulo sin detener el proceso")
    void testPreloadImagesHandlesErrorsGracefully() throws InterruptedException {
        List<String> chapterIds = List.of("chapter-1", "chapter-2", "chapter-3");
        
        Mockito.when(imgService.getImg("chapter-1")).thenReturn(List.of());
        Mockito.when(imgService.getImg("chapter-2")).thenThrow(new RuntimeException("Error simulado"));
        Mockito.when(imgService.getImg("chapter-3")).thenReturn(List.of());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
            imgPreloadService.preloadImages(chapterIds);
            try {
                // Esperar a que se procese
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        
        // Verificar que se intentó procesar todos los capítulos
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-1");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-2");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-3");
    }

    @Test
    @DisplayName("preloadImages - Debe procesar múltiples lotes de capítulos")
    void testPreloadImagesProcessesMultipleBatches() throws InterruptedException {
        List<String> firstBatch = List.of("chapter-1", "chapter-2");
        List<String> secondBatch = List.of("chapter-3", "chapter-4");
        
        Mockito.when(imgService.getImg(ArgumentMatchers.any(String.class))).thenReturn(List.of());
        
        CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
            imgPreloadService.preloadImages(firstBatch);
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            imgPreloadService.preloadImages(secondBatch);
            
            try {
                // Esperar a que se procese todo
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        }).start();
        
        Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
        
        // Verificar que todos los capítulos fueron procesados
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-1");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-2");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-3");
        Mockito.verify(imgService, Mockito.timeout(2000).atLeastOnce()).getImg("chapter-4");
    }
}

