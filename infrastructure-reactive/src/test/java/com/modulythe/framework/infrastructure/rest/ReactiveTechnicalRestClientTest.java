package com.modulythe.framework.infrastructure.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReactiveTechnicalRestClientTest {

    private ReactiveTechnicalRestClient client;
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setUp() {
        exchangeFunction = mock(ExchangeFunction.class);
        WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
        client = new ReactiveTechnicalRestClient(webClientBuilder);
    }

    @Test
    void shouldPostDataSuccessfully() {
        // Given
        String url = "http://localhost/api/resource";
        String token = "my-token";
        String requestBody = "request";
        String responseBody = "response";

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "text/plain")
                .body(responseBody)
                .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(mockResponse));

        // When
        Mono<ResponseEntity<String>> responseMono = client.postData(url, token, requestBody, String.class);

        // Then
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("response", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorOnPost() {
        // Given
        String url = "http://localhost/api/error";
        
        WebClientResponseException exception = 
                WebClientResponseException.BadRequest.create(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        null,
                        null,
                        null
                );

        when(exchangeFunction.exchange(any())).thenReturn(Mono.error(exception));

        // When
        Mono<ResponseEntity<String>> responseMono = client.postData(url, "token", "body", String.class);

        // Then
        StepVerifier.create(responseMono)
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();
    }
}
