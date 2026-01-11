package com.modulythe.framework.application.security;

import reactor.core.publisher.Mono;

public interface ReactiveTokenExchangePort {
    Mono<TokenResponse> exchange(String assertion);
}
