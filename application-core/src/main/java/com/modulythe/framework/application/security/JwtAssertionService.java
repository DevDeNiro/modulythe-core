package com.modulythe.framework.application.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Objects;

/**
 * Service responsible for creating and signing JWT assertions.
 * <p>
 * This service is typically used in the OAuth2 Client Credentials flow, where a self-signed
 * JWT is exchanged for an access token. It uses the Nimbus JOSE library for signing.
 * </p>
 */
@Service
public class JwtAssertionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAssertionService.class);
    private static final String SCOPE_CLAIM = "scope";

    /**
     * Signs a JWT with the provided configuration.
     *
     * @param config The configuration containing client details and keystore information.
     * @return A serialized, signed JWT string (JWS).
     * @throws JwtSigningException if the JWT cannot be created or signed.
     */
    public String signJwt(JwtAssertionConfig config) {
        validateConfig(config);

        PrivateKey privateKey = loadPrivateKey(config);
        return createAndSignJwt(config, privateKey);
    }

    private void validateConfig(JwtAssertionConfig config) {
        Objects.requireNonNull(config, "JwtAssertionConfig cannot be null");
        Objects.requireNonNull(config.clientId(), "Client ID cannot be null");
        Objects.requireNonNull(config.audienceUrl(), "Audience URL cannot be null");
        Objects.requireNonNull(config.keyAlias(), "Key alias cannot be null");
        Objects.requireNonNull(config.trustStorePath(), "Trust store path cannot be null");
        Objects.requireNonNull(config.keyStorePassword(), "KeyStore password cannot be null");
    }

    private PrivateKey loadPrivateKey(JwtAssertionConfig config) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            LOGGER.error("Failed to initialize KeyStore instance");
            throw new JwtSigningException("KeyStore initialization failed", e);
        }

        File file;
        try {
            file = ResourceUtils.getFile(config.trustStorePath());
        } catch (FileNotFoundException e) {
            LOGGER.error("Trust store file not found: {}", config.trustStorePath());
            throw new JwtSigningException("Trust store file not found: " + config.trustStorePath(), e);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            keyStore.load(inputStream, config.keyStorePassword().toCharArray());
        } catch (IOException e) {
            LOGGER.error("Failed to read trust store file: {}", config.trustStorePath());
            throw new JwtSigningException("Failed to read trust store file", e);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            LOGGER.error("Failed to load trust store - invalid format or corrupted: {}", config.trustStorePath());
            throw new JwtSigningException("Trust store is corrupted or has invalid format", e);
        }

        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(config.keyAlias(), config.keyStorePassword().toCharArray());
            if (privateKey == null) {
                LOGGER.error("Private key not found for alias: {}", config.keyAlias());
                throw new JwtSigningException("Private key not found for alias: " + config.keyAlias());
            }
            return privateKey;
        } catch (KeyStoreException e) {
            LOGGER.error("KeyStore not initialized properly");
            throw new JwtSigningException("KeyStore not initialized", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Algorithm for recovering the key not available");
            throw new JwtSigningException("Key recovery algorithm not available", e);
        } catch (UnrecoverableKeyException e) {
            LOGGER.error("Failed to recover private key - wrong password or corrupted key for alias: {}", config.keyAlias());
            throw new JwtSigningException("Failed to recover private key - check password and key integrity", e);
        }
    }

    private String createAndSignJwt(JwtAssertionConfig config, PrivateKey privateKey) {
        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(config.keyAlias()).build(),
                    new JWTClaimsSet.Builder()
                            .issuer(config.clientId())
                            .subject(config.clientId())
                            .audience(config.audienceUrl())
                            .expirationTime(new Date(System.currentTimeMillis() + config.expireTimeInSeconds() * 1000L))
                            .issueTime(new Date(System.currentTimeMillis() + config.issuedAtOffsetSeconds() * 1000L))
                            .claim(SCOPE_CLAIM, config.scope())
                            .build());

            signedJWT.sign(new RSASSASigner(privateKey));
            LOGGER.debug("JWT assertion signed successfully for client: {}", config.clientId());
            return signedJWT.serialize();

        } catch (JOSEException e) {
            LOGGER.error("Failed to sign JWT assertion for client: {}", config.clientId());
            throw new JwtSigningException("Failed to sign JWT assertion", e);
        }
    }

    /**
     * Custom exception for JWT signing failures with specific error information.
     */
    public static class JwtSigningException extends RuntimeException {
        public JwtSigningException(String message) {
            super(message);
        }

        public JwtSigningException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Configuration record for generating a JWT assertion.
     *
     * @param clientId              The client identifier (Issuer and Subject of the JWT).
     * @param audienceUrl           The audience URL (Audience of the JWT).
     * @param keyAlias              The alias of the private key in the keystore.
     * @param trustStorePath        The file path to the keystore.
     * @param keyStorePassword      The password for the keystore and private key.
     * @param scope                 The scope to include in the JWT.
     * @param expireTimeInSeconds   How long the token is valid (seconds).
     * @param issuedAtOffsetSeconds Time offset for 'issued at' to handle clock skew (seconds).
     */
    public record JwtAssertionConfig(
            String clientId,
            String audienceUrl,
            String keyAlias,
            String trustStorePath,
            String keyStorePassword,
            String scope,
            int expireTimeInSeconds,
            int issuedAtOffsetSeconds
    ) {
    }
}
