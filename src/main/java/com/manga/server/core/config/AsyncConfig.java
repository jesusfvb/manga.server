
package com.manga.server.core.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean(name = "globalExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Número mínimo de hilos
        executor.setMaxPoolSize(20); // Máximo número de hilos
        executor.setQueueCapacity(100); // Tareas que pueden esperar
        executor.setThreadNamePrefix("AsyncManga-");
        executor.initialize();
        return executor;
    }
}
