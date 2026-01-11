package com.modulythe.framework.application.security;

public interface TokenExchangePort {
    TokenResponse exchange(String assertion);
}
