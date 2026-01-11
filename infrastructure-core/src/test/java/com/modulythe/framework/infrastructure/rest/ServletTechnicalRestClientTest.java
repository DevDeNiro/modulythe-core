package com.modulythe.framework.infrastructure.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ServletTechnicalRestClientTest {

    private ServletTechnicalRestClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new ServletTechnicalRestClient(restTemplate);
    }

    @Test
    void shouldPostDataSuccessfully() {
        // Given
        String url = "/api/resource";
        String token = "my-token";
        String requestBody = "request";
        String responseBody = "response";

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer my-token"))
                .andExpect(content().string("request"))
                .andRespond(withSuccess(responseBody, MediaType.TEXT_PLAIN));

        // When
        ResponseEntity<String> response = client.postData(url, token, requestBody, String.class);

        // Then
        server.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("response", response.getBody());
    }

    @Test
    void shouldHandleErrorOnPost() {
        // Given
        String url = "/api/error";
        String token = "token";

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // When & Then
        assertThrows(HttpClientErrorException.class, () -> 
            client.postData(url, token, "body", String.class)
        );
        server.verify();
    }
}
