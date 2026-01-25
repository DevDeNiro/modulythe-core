package com.modulythe.framework.application.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for outbound client security.
 * <p>
 * This class maps properties prefixed with "modulythe.security.client" to configure
 * the OAuth2 Client Credentials flow, used for service-to-service authentication.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "modulythe.security.client")
public class ClientSecurityProperties {

    /**
     * Enable or disable the client security interceptor.
     */
    private boolean enabled = false;
    /**
     * The client ID used for authentication.
     */
    private String clientId;
    /**
     * The audience URL for the JWT assertion.
     */
    private String audienceUrl;
    /**
     * The alias of the key in the keystore used to sign the assertion.
     */
    private String keyAlias;
    /**
     * Path to the trust store/keystore containing the private key.
     */
    private String trustStorePath;
    /**
     * Password for the keystore.
     */
    private String keyStorePassword;
    /**
     * Scopes to request in the access token. Default is "read write".
     */
    private String scope = "read write";
    /**
     * Expiration time for the signed JWT assertion in seconds. Default is 300 (5 minutes).
     */
    private int expireTimeInSeconds = 300;
    /**
     * Offset in seconds for the "issued at" (iat) claim to account for clock skew. Default is -30.
     */
    private int issuedAtOffsetSeconds = -30;

    // Optional caller ID for tracking
    /**
     * The header key for the Caller ID. Default is "X-Caller-ID".
     */
    private String callerIdKey = "X-Caller-ID";
    /**
     * The value for the Caller ID to identify this service in downstream calls.
     */
    private String callerIdValue;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAudienceUrl() {
        return audienceUrl;
    }

    public void setAudienceUrl(String audienceUrl) {
        this.audienceUrl = audienceUrl;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpireTimeInSeconds() {
        return expireTimeInSeconds;
    }

    public void setExpireTimeInSeconds(int expireTimeInSeconds) {
        this.expireTimeInSeconds = expireTimeInSeconds;
    }

    public int getIssuedAtOffsetSeconds() {
        return issuedAtOffsetSeconds;
    }

    public void setIssuedAtOffsetSeconds(int issuedAtOffsetSeconds) {
        this.issuedAtOffsetSeconds = issuedAtOffsetSeconds;
    }

    public String getCallerIdKey() {
        return callerIdKey;
    }

    public void setCallerIdKey(String callerIdKey) {
        this.callerIdKey = callerIdKey;
    }

    public String getCallerIdValue() {
        return callerIdValue;
    }

    public void setCallerIdValue(String callerIdValue) {
        this.callerIdValue = callerIdValue;
    }
}
