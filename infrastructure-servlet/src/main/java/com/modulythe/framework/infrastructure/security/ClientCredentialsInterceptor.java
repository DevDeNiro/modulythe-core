package com.modulythe.framework.infrastructure.security;

import com.modulythe.framework.application.security.ClientSecurityProperties;
import com.modulythe.framework.application.security.JwtAssertionService;
import com.modulythe.framework.application.security.SecurityAuditLogger;
import com.modulythe.framework.application.security.TokenExchangePort;
import com.modulythe.framework.application.security.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * HTTP request interceptor for adding OAuth2 Client Credentials authentication.
 * <p>
 * This interceptor handles token caching, automatic refresh, and retry with exponential backoff.
 * </p>
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ClientCredentialsInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCredentialsInterceptor.class);

    private final ClientSecurityProperties properties;
    private final JwtAssertionService jwtAssertionService;
    private final TokenExchangePort tokenExchangePort;
    private final SecurityAuditLogger securityAuditLogger;

    // Thread-safe atomic reference to hold the current token
    private final AtomicReference<TokenResponse> cachedToken = new AtomicReference<>();

    // Retry state for exponential backoff
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile Instant nextRetryTime = Instant.MIN;

    @Value("${modulythe.security.client.max-retries:5}")
    private int maxRetries;

    @Value("${modulythe.security.client.initial-backoff-ms:1000}")
    private long initialBackoffMs;

    @Value("${modulythe.security.client.max-backoff-ms:60000}")
    private long maxBackoffMs;

    public ClientCredentialsInterceptor(
            ClientSecurityProperties properties,
            JwtAssertionService jwtAssertionService,
            TokenExchangePort tokenExchangePort,
            SecurityAuditLogger securityAuditLogger) {
        this.properties = properties;
        this.jwtAssertionService = jwtAssertionService;
        this.tokenExchangePort = tokenExchangePort;
        this.securityAuditLogger = securityAuditLogger;
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
        } else {
            LOGGER.warn("No valid token available for request to: {}", request.getURI());
        }

        return execution.execute(request, body);
    }

    private TokenResponse getToken() {
        TokenResponse currentToken = cachedToken.get();
        if (isValid(currentToken)) {
            return currentToken;
        }

        // Check if we should wait before retrying (backoff)
        if (Instant.now().isBefore(nextRetryTime)) {
            LOGGER.debug("Skipping token refresh due to backoff. Next retry at: {}", nextRetryTime);
            return null;
        }

        // Double-checked locking pattern using synchronized block
        synchronized (this) {
            currentToken = cachedToken.get();
            if (isValid(currentToken)) {
                return currentToken;
            }

            // Re-check backoff inside synchronized block
            if (Instant.now().isBefore(nextRetryTime)) {
                return null;
            }

            return refreshTokenWithRetry();
        }
    }

    private TokenResponse refreshTokenWithRetry() {
        int failures = consecutiveFailures.get();

        // Check if max retries exceeded
        if (failures >= maxRetries) {
            LOGGER.error("Max retries ({}) exceeded for token refresh. Waiting for backoff period.", maxRetries);
            securityAuditLogger.logSecurityEvent(
                    SecurityAuditLogger.SecurityEventType.RATE_LIMIT_EXCEEDED,
                    properties.getClientId(),
                    null,
                    "Token refresh max retries exceeded"
            );
            return null;
        }

        try {
            TokenResponse response = refreshToken();

            // Success - reset failure counter
            consecutiveFailures.set(0);
            nextRetryTime = Instant.MIN;

            securityAuditLogger.logSecurityEvent(
                    SecurityAuditLogger.SecurityEventType.TOKEN_REFRESH_SUCCESS,
                    properties.getClientId(),
                    null,
                    "Token refreshed successfully"
            );

            return response;
        } catch (Exception e) {
            // Failure - increment counter and calculate backoff
            int newFailures = consecutiveFailures.incrementAndGet();
            long backoffMs = calculateBackoff(newFailures);
            nextRetryTime = Instant.now().plusMillis(backoffMs);

            LOGGER.error("Token refresh failed (attempt {}/{}). Next retry in {}ms",
                    newFailures, maxRetries, backoffMs, e);

            securityAuditLogger.logSecurityEvent(
                    SecurityAuditLogger.SecurityEventType.TOKEN_REFRESH_FAILURE,
                    properties.getClientId(),
                    null,
                    "Token refresh failed: " + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Calculates exponential backoff with jitter.
     *
     * @param failureCount The number of consecutive failures.
     * @return The backoff time in milliseconds.
     */
    private long calculateBackoff(int failureCount) {
        // Exponential backoff: initialBackoff * 2^(failures-1)
        long exponentialBackoff = initialBackoffMs * (1L << Math.min(failureCount - 1, 10));
        // Cap at max backoff
        long cappedBackoff = Math.min(exponentialBackoff, maxBackoffMs);
        // Add jitter (0-25% of backoff)
        long jitter = (long) (cappedBackoff * 0.25 * Math.random());
        return cappedBackoff + jitter;
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

    /**
     * Scheduled token refresh to ensure a valid token is always available.
     * Uses backoff-aware refresh to prevent overwhelming the auth server.
     */
    @Scheduled(fixedRateString = "${modulythe.security.client.refresh-rate:3000000}") // Default 50 mins
    public void scheduledRefresh() {
        if (!properties.isEnabled()) {
            return;
        }

        // Skip if in backoff period
        if (Instant.now().isBefore(nextRetryTime)) {
            LOGGER.debug("Scheduled refresh skipped due to backoff");
            return;
        }

        synchronized (this) {
            try {
                TokenResponse response = refreshToken();
                if (response != null) {
                    consecutiveFailures.set(0);
                    nextRetryTime = Instant.MIN;
                }
            } catch (Exception e) {
                int newFailures = consecutiveFailures.incrementAndGet();
                long backoffMs = calculateBackoff(newFailures);
                nextRetryTime = Instant.now().plusMillis(backoffMs);

                LOGGER.error("Scheduled token refresh failed. Next retry in {}ms", backoffMs, e);
                cachedToken.set(null); // Force fetch on next request
            }
        }
    }

    /**
     * Resets the retry state. Useful for testing or manual recovery.
     */
    public void resetRetryState() {
        consecutiveFailures.set(0);
        nextRetryTime = Instant.MIN;
        LOGGER.info("Token refresh retry state has been reset");
    }
}
