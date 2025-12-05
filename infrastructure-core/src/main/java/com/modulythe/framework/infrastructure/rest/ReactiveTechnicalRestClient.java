package com.modulythe.framework.infrastructure.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RefreshScope
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveTechnicalRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveTechnicalRestClient.class);

    private static final String EMPTY_TOKEN = "Token d'authentification vide ou null pour l'appel REST {}";
    private static final String ERROR_CALLING_REST = "Erreur lors de l'appel REST {}";

    private final WebClient webClient;

    public ReactiveTechnicalRestClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public <T> Mono<ResponseEntity<T>> postData(@NonNull String url, @Nullable String token, @Nullable Object bodyRequest, @NonNull Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .headers(headers -> headers.addAll(createHttpHeaders(url, token)))
                .bodyValue(bodyRequest != null ? bodyRequest : "")
                .retrieve()
                .toEntity(responseType)
                .doOnError(WebClientResponseException.class, e -> LOGGER.error(ERROR_CALLING_REST, url, e));
    }

    public <T> Mono<ResponseEntity<T>> getData(@NonNull String url, @Nullable String token, @NonNull Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(createHttpHeaders(url, token)))
                .retrieve()
                .toEntity(responseType)
                .doOnError(WebClientResponseException.class, e -> LOGGER.error(ERROR_CALLING_REST, url, e));
    }

    public <T> Mono<ResponseEntity<T>> putData(@NonNull String url, @Nullable String token, @Nullable Object bodyRequest, @NonNull Class<T> responseType) {
        return webClient.put()
                .uri(url)
                .headers(headers -> headers.addAll(createHttpHeaders(url, token)))
                .bodyValue(bodyRequest != null ? bodyRequest : "")
                .retrieve()
                .toEntity(responseType)
                .doOnError(WebClientResponseException.class, e -> LOGGER.error(ERROR_CALLING_REST, url, e));
    }

    public <T> Mono<ResponseEntity<T>> deleteData(@NonNull String url, @Nullable String token, @NonNull Class<T> responseType) {
        return webClient.method(HttpMethod.DELETE)
                .uri(url)
                .headers(headers -> headers.addAll(createHttpHeaders(url, token)))
                .retrieve()
                .toEntity(responseType)
                .doOnError(WebClientResponseException.class, e -> LOGGER.error(ERROR_CALLING_REST, url, e));
    }

    private HttpHeaders createHttpHeaders(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token == null || token.isEmpty()) {
            LOGGER.error(EMPTY_TOKEN, url);
        } else {
            headers.setBearerAuth(token);
        }
        return headers;
    }
}
