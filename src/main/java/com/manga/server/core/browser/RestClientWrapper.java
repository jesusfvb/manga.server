
package com.manga.server.core.browser;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestClientWrapper {

    private final RestClient restClient;

    public RestClientWrapper() {
        this.restClient = RestClient.create();
    }

    public <T> T get(String url, TypeReference<T> typeReference)
            throws JsonProcessingException {

        if (url == null || url.isEmpty()) {
            return null;
        }

        var result = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        return new ObjectMapper().readValue(result, typeReference);
    }
}