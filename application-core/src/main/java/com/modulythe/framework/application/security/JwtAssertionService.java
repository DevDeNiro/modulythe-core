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

@Service
public class JwtAssertionService {

    private static final String SCOPE_CLAIM = "scope";

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
