package com.modulythe.framework.application.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * General security configuration properties.
 * <p>
 * Maps properties prefixed with "modulythe.security".
 * Controls the authentication mode (JWT vs Opaque) and introspection details for Resource Servers.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "modulythe.security")
public class SecurityProperties {

    /**
     * Authentication mode: "jwt" (default) or "opaque".
     */
    private String mode = "jwt"; // jwt or opaque
    /**
     * URI for opaque token introspection (only used if mode is "opaque").
     */
    private String introspectionUri;
    /**
     * Client ID for introspection (only used if mode is "opaque").
     */
    private String clientId;
    /**
     * Client Secret for introspection (only used if mode is "opaque").
     */
    private String clientSecret;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getIntrospectionUri() {
        return introspectionUri;
    }

    public void setIntrospectionUri(String introspectionUri) {
        this.introspectionUri = introspectionUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
