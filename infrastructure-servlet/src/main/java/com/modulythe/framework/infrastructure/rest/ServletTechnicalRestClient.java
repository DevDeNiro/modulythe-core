package com.modulythe.framework.infrastructure.rest;

import com.modulythe.framework.infrastructure.common.security.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Technical REST client for Servlet-based applications.
 * <p>
 * This client includes SSRF protection by validating URLs before making requests.
 * SSRF validation can be disabled via configuration for trusted internal calls.
 * </p>
 */
@Service
@RefreshScope
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletTechnicalRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletTechnicalRestClient.class);

    private static final String EMPTY_TOKEN = "Token d'authentification vide ou null pour l'appel REST {}";
    private static final String ERROR_CALLING_REST = "Erreur lors de l'appel REST {}";

    private final RestTemplate restTemplate;

    /**
     * Enable/disable SSRF protection. Disable only for trusted internal service-to-service calls.
     */
    @Value("${modulythe.rest.ssrf-protection:true}")
    private boolean ssrfProtectionEnabled;

    public ServletTechnicalRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> postData(String url, String token, Object bodyRequest, Class<T> responseType) throws HttpClientErrorException {
        validateRequest(url, responseType);

        HttpHeaders headers = createHttpHeaders(url, token);
        HttpEntity<Object> entity = new HttpEntity<>(bodyRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        } catch (HttpClientErrorException e) {
            LOGGER.error(ERROR_CALLING_REST, url, e);
            throw e;
        }
    }

    public <T> ResponseEntity<T> getData(String url, String token, Class<T> responseType) throws HttpClientErrorException {
        validateRequest(url, responseType);

        HttpHeaders headers = createHttpHeaders(url, token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        } catch (HttpClientErrorException e) {
            LOGGER.error(ERROR_CALLING_REST, url, e);
            throw e;
        }
    }

    public <T> ResponseEntity<T> putData(String url, String token, Object bodyRequest, Class<T> responseType) throws HttpClientErrorException {
        validateRequest(url, responseType);

        HttpHeaders headers = createHttpHeaders(url, token);
        HttpEntity<Object> entity = new HttpEntity<>(bodyRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        } catch (HttpClientErrorException e) {
            LOGGER.error(ERROR_CALLING_REST, url, e);
            throw e;
        }
    }

    public <T> ResponseEntity<T> deleteData(String url, String token, Class<T> responseType) throws HttpClientErrorException {
        validateRequest(url, responseType);

        HttpHeaders headers = createHttpHeaders(url, token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
        } catch (HttpClientErrorException e) {
            LOGGER.error(ERROR_CALLING_REST, url, e);
            throw e;
        }
    }

    /**
     * Validates the request parameters including SSRF protection.
     *
     * @param url          The target URL.
     * @param responseType The expected response type.
     * @throws NullPointerException if url or responseType is null.
     * @throws SecurityException    if SSRF protection is enabled and URL targets internal/forbidden hosts.
     */
    private <T> void validateRequest(String url, Class<T> responseType) {
        Objects.requireNonNull(url, "URL cannot be null");
        Objects.requireNonNull(responseType, "Response type cannot be null");

        if (ssrfProtectionEnabled) {
            try {
                UrlValidator.validateUrl(url);
            } catch (SecurityException e) {
                LOGGER.warn("SSRF protection blocked request to URL: {}", url);
                throw e;
            }
        }
    }

    private HttpHeaders createHttpHeaders(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token == null || token.isEmpty()) {
            LOGGER.warn(EMPTY_TOKEN, url);
        } else {
            headers.setBearerAuth(token);
        }
        return headers;
    }
}
