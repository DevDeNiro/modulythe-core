package com.modulythe.framework.infrastructure.security;

import com.modulythe.framework.application.security.ClientSecurityProperties;
import com.modulythe.framework.application.security.JwtAssertionService;
import com.modulythe.framework.application.security.TokenExchangePort;
import com.modulythe.framework.application.security.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ClientCredentialsInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCredentialsInterceptor.class);

    private final ClientSecurityProperties properties;
    private final JwtAssertionService jwtAssertionService;
    private final TokenExchangePort tokenExchangePort;

    // Thread-safe atomic reference to hold the current token
    private final AtomicReference<TokenResponse> cachedToken = new AtomicReference<>();

    public ClientCredentialsInterceptor(
            ClientSecurityProperties properties,
            JwtAssertionService jwtAssertionService,
            TokenExchangePort tokenExchangePort) {
        this.properties = properties;
        this.jwtAssertionService = jwtAssertionService;
        this.tokenExchangePort = tokenExchangePort;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (!properties.isEnabled()) {
            return execution.execute(request, body);
        }

        TokenResponse token = getToken();
        if (token != null) {
            String authorizationToken = token.getTokenType() + " " + token.getAccessToken();
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationToken);

            if (StringUtils.hasText(properties.getCallerIdValue())) {
                request.getHeaders().set(properties.getCallerIdKey(), properties.getCallerIdValue().trim());
            }
        }

        return execution.execute(request, body);
    }

    private TokenResponse getToken() {
        TokenResponse currentToken = cachedToken.get();
        if (isValid(currentToken)) {
            return currentToken;
        }

        // Double-checked locking pattern logic using synchronized block
        synchronized (this) {
            currentToken = cachedToken.get();
            if (isValid(currentToken)) {
                return currentToken;
            }

            try {
                return refreshToken();
            } catch (Exception e) {
                LOGGER.error("Failed to refresh access token", e);
                return null;
            }
        }
    }

    private boolean isValid(TokenResponse token) {
        if (token == null) {
            return false;
        }

        long safetyMarginSeconds = 30;
        Instant expirationTime = token.getReceivedAt().plusSeconds(token.getExpiresIn());
        return Instant.now().plusSeconds(safetyMarginSeconds).isBefore(expirationTime);
    }

    private TokenResponse refreshToken() {
        LOGGER.debug("Refreshing access token...");
        JwtAssertionService.JwtAssertionConfig config = new JwtAssertionService.JwtAssertionConfig(
                properties.getClientId(),
                properties.getAudienceUrl(),
                properties.getKeyAlias(),
                properties.getTrustStorePath(),
                properties.getKeyStorePassword(),
                properties.getScope(),
                properties.getExpireTimeInSeconds(),
                properties.getIssuedAtOffsetSeconds()
        );

        String assertion = jwtAssertionService.signJwt(config);
        TokenResponse response = tokenExchangePort.exchange(assertion);

        cachedToken.set(response);
        LOGGER.debug("Access token refreshed successfully.");
        return response;
    }

    // Optional: Periodic refresh to ensure valid token is always available (e.g. every 50 minutes if TTL is 1 hour)
    // Or simpler: Evict periodically
    @Scheduled(fixedRateString = "${modulythe.security.client.refresh-rate:3000000}") // Default 50 mins
    public void scheduledRefresh() {
        if (properties.isEnabled()) {
            try {
                refreshToken();
            } catch (Exception e) {
                LOGGER.error("Error during scheduled token refresh", e);
                cachedToken.set(null); // Force fetch on next request
            }
        }
    }
}
