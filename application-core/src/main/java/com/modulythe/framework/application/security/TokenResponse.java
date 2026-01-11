package com.modulythe.framework.application.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents the response from an OAuth2 token endpoint.
 * <p>
 * Contains the access token and related metadata (type, expiration, scope).
 * It also tracks the reception time to calculate validity.
 * </p>
 */
public class TokenResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("scope")
    private String scope;

    /**
     * Timestamp when this response was instantiated/received.
     * Used to calculate local expiration.
     */
    @JsonIgnore
    private Instant receivedAt;

    public TokenResponse() {
        this.receivedAt = Instant.now();
    }

    public TokenResponse(String accessToken, String tokenType, Integer expiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.receivedAt = Instant.now();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }
}
