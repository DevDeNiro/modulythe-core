package com.modulythe.framework.application.security;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtAssertionServiceTest {

    private JwtAssertionService jwtAssertionService;
    private String keystorePath;

    @BeforeEach
    void setUp() throws Exception {
        jwtAssertionService = new JwtAssertionService();
        File file = ResourceUtils.getFile("classpath:test-keystore.jks");
        keystorePath = file.getAbsolutePath();
    }

    @Test
    void shouldSignJwtCorrectly() throws Exception {
        // Given
        var config = new JwtAssertionService.JwtAssertionConfig(
                "test-client-id",
                "https://audience.url",
                "test-key",
                keystorePath,
                "changeit",
                "read write",
                300,
                0
        );

        // When
        String token = jwtAssertionService.signJwt(config);

        // Then
        assertNotNull(token);

        SignedJWT signedJWT = SignedJWT.parse(token);
        
        // Verify Claims
        assertEquals("test-client-id", signedJWT.getJWTClaimsSet().getIssuer());
        assertEquals("test-client-id", signedJWT.getJWTClaimsSet().getSubject());
        assertEquals("https://audience.url", signedJWT.getJWTClaimsSet().getAudience().get(0));
        assertEquals("read write", signedJWT.getJWTClaimsSet().getStringClaim("scope"));
        assertTrue(signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date()));

        // Verify Signature
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, "changeit".toCharArray());
        }
        Certificate cert = keyStore.getCertificate("test-key");
        RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

        assertTrue(signedJWT.verify(new RSASSAVerifier(publicKey)), "Signature should be valid");
    }

    @Test
    void shouldFailWithInvalidKeystorePath() {
        // Given
        var config = new JwtAssertionService.JwtAssertionConfig(
                "test-client-id",
                "url",
                "test-key",
                "invalid/path.jks",
                "changeit",
                "scope",
                300,
                0
        );

        // When & Then
        assertThrows(IllegalStateException.class, () -> jwtAssertionService.signJwt(config));
    }
    
    @Test
    void shouldFailWithInvalidPassword() {
         // Given
        var config = new JwtAssertionService.JwtAssertionConfig(
                "test-client-id",
                "url",
                "test-key",
                keystorePath,
                "wrongpassword",
                "scope",
                300,
                0
        );

        // When & Then
        assertThrows(IllegalStateException.class, () -> jwtAssertionService.signJwt(config));
    }
}
