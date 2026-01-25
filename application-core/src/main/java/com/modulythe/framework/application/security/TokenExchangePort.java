package com.modulythe.framework.application.security;

/**
 * Port for exchanging a signed assertion for an access token (Blocking/Servlet).
 * <p>
 * This interface defines the contract for the OAuth2 Token Exchange (or Client Credentials with assertion)
 * in a blocking way, suitable for Servlet applications.
 * </p>
 */
public interface TokenExchangePort {
    /**
     * Exchanges a signed assertion for an access token.
     *
     * @param assertion The signed JWT assertion.
     * @return The {@link TokenResponse} containing the access token.
     */
    TokenResponse exchange(String assertion);
}
