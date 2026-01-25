package com.modulythe.framework.infrastructure.common.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    @Test
    void shouldAcceptValidHttpsUrl() {
        assertDoesNotThrow(() -> UrlValidator.validateUrl("https://api.example.com/v1/users"));
    }

    @Test
    void shouldAcceptValidHttpUrl() {
        assertDoesNotThrow(() -> UrlValidator.validateUrl("http://api.example.com/v1/users"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost/api",
            "http://127.0.0.1/api",
            "http://localhost:8080/api",
            "http://127.0.0.1:8080/api"
    })
    void shouldBlockLocalhostUrls(String url) {
        SecurityException exception = assertThrows(SecurityException.class,
                () -> UrlValidator.validateUrl(url));
        assertTrue(exception.getMessage().contains("SSRF protection"));
    }

    @Test
    void shouldBlockAwsMetadataEndpoint() {
        SecurityException exception = assertThrows(SecurityException.class,
                () -> UrlValidator.validateUrl("http://169.254.169.254/latest/meta-data/"));
        assertTrue(exception.getMessage().contains("SSRF protection"));
    }

    @Test
    void shouldBlockGcpMetadataEndpoint() {
        SecurityException exception = assertThrows(SecurityException.class,
                () -> UrlValidator.validateUrl("http://metadata.google.internal/computeMetadata/v1/"));
        assertTrue(exception.getMessage().contains("SSRF protection"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ftp://files.example.com/file.txt",
            "file:///etc/passwd"
    })
    void shouldBlockNonHttpSchemes(String url) {
        SecurityException exception = assertThrows(SecurityException.class,
                () -> UrlValidator.validateUrl(url));
        assertTrue(exception.getMessage().contains("scheme not allowed"));
    }

    @Test
    void shouldRejectUnknownProtocol() {
        // Protocols like gopher are not recognized by Java's URL class and throw MalformedURLException
        assertThrows(IllegalArgumentException.class,
                () -> UrlValidator.validateUrl("gopher://example.com/"));
    }

    @Test
    void shouldRejectNullUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> UrlValidator.validateUrl(null));
    }

    @Test
    void shouldRejectEmptyUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> UrlValidator.validateUrl(""));
    }

    @Test
    void shouldRejectBlankUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> UrlValidator.validateUrl("   "));
    }

    @Test
    void shouldRejectMalformedUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> UrlValidator.validateUrl("not-a-valid-url"));
    }

    @Test
    void shouldReturnTrueForSafeUrl() {
        assertTrue(UrlValidator.isSafeUrl("https://api.example.com"));
    }

    @Test
    void shouldReturnFalseForUnsafeUrl() {
        assertFalse(UrlValidator.isSafeUrl("http://localhost"));
    }

    @Test
    void shouldReturnFalseForInvalidUrl() {
        assertFalse(UrlValidator.isSafeUrl("not-a-url"));
    }

    @Test
    void shouldReturnFalseForNullUrl() {
        assertFalse(UrlValidator.isSafeUrl(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://0.0.0.0/api",
            "http://[::1]/api"
    })
    void shouldBlockOtherLoopbackAddresses(String url) {
        SecurityException exception = assertThrows(SecurityException.class,
                () -> UrlValidator.validateUrl(url));
        assertTrue(exception.getMessage().contains("SSRF protection"));
    }
}
