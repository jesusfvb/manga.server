package com.manga.server.core.filtres;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RateLimitFilter extends OncePerRequestFilter {

    // Almacena un bucket por endpoint (URI)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Configuración: 5 requests por minuto
    private static final int REQUESTS_PER_MINUTE = 5;
    private static final int MINUTES = 1;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String endpoint = request.getRequestURI();
        
        // Obtener o crear el bucket para este endpoint
        Bucket bucket = buckets.computeIfAbsent(endpoint, this::createBucket);

        // Intentar consumir un token
        if (bucket.tryConsume(1)) {
            // Hay tokens disponibles, continuar con la petición
            filterChain.doFilter(request, response);
        } else {
            // No hay tokens disponibles, retornar 429 Too Many Requests
            log.warn("Rate limit excedido para endpoint: {} desde IP: {}", endpoint, request.getRemoteAddr());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit excedido. Máximo 5 requests por minuto por endpoint.\"}");
        }
    }

    private Bucket createBucket(String endpoint) {
        // Crear un bucket con 5 tokens que se recargan a razón de 5 por minuto
        Bandwidth limit = Bandwidth.builder()
                .capacity(REQUESTS_PER_MINUTE)
                .refillIntervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(MINUTES))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Limpia todos los buckets. Útil para tests.
     */
    public void reset() {
        buckets.clear();
    }
}

