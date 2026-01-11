package com.modulythe.framework.application.security;

import reactor.core.publisher.Mono;

/**
 * Port for exchanging a signed assertion for an access token in a reactive context.
 * <p>
 * This interface defines the contract for the OAuth2 Token Exchange (or Client Credentials with assertion)
 * in a non-blocking way, suitable for WebFlux applications.
 * </p>
 */
public interface ReactiveTokenExchangePort {
    /**
     * Exchanges a signed assertion for an access token.
     *
     * @param assertion The signed JWT assertion.
     * @return A Mono emitting the {@link TokenResponse} containing the access token.
     */
    Mono<TokenResponse> exchange(String assertion);
}
