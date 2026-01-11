package com.modulythe.framework.application.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Date;

/**
 * Service responsible for creating and signing JWT assertions.
 * <p>
 * This service is typically used in the OAuth2 Client Credentials flow, where a self-signed
 * JWT is exchanged for an access token. It uses the Nimbus JOSE library for signing.
 * </p>
 */
@Service
public class JwtAssertionService {

    private static final String SCOPE_CLAIM = "scope";

    /**
     * Signs a JWT with the provided configuration.
     *
     * @param config The configuration containing client details and keystore information.
     * @return A serialized, signed JWT string (JWS).
     * @throws IllegalStateException if the JWT cannot be created or signed (e.g., keystore errors).
     */
    public String signJwt(JwtAssertionConfig config) {
        try {
            // Load the private key
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            File file = ResourceUtils.getFile(config.trustStorePath());
            try (InputStream inputStream = new FileInputStream(file)) {
                keyStore.load(inputStream, config.keyStorePassword().toCharArray());
            }

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(config.keyAlias(), config.keyStorePassword().toCharArray());

            // Create the JWT
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
            return signedJWT.serialize();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign JWT assertion", e);
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
