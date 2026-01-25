package com.modulythe.framework.infrastructure.common.security;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * Utility class for validating URLs to prevent Server-Side Request Forgery (SSRF) attacks.
 * <p>
 * This validator checks that URLs do not target internal/private network addresses
 * or potentially dangerous hosts.
 * </p>
 */
public final class UrlValidator {

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost",
            "127.0.0.1",
            "0.0.0.0",
            "::1",
            "[::1]",
            "metadata.google.internal",      // GCP metadata
            "169.254.169.254"                // AWS/Azure/GCP metadata endpoint
    );

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    private UrlValidator() {
        // Utility class
    }

    /**
     * Validates a URL for SSRF vulnerabilities.
     *
     * @param urlString The URL string to validate.
     * @throws SecurityException if the URL is potentially dangerous (SSRF risk).
     * @throws IllegalArgumentException if the URL is malformed.
     */
    public static void validateUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + urlString, e);
        }

        validateScheme(url);
        validateHost(url);
    }

    private static void validateScheme(URL url) {
        String scheme = url.getProtocol().toLowerCase();
        if (!ALLOWED_SCHEMES.contains(scheme)) {
            throw new SecurityException("URL scheme not allowed: " + scheme + ". Only HTTP(S) is permitted.");
        }
    }

    private static void validateHost(URL url) {
        String host = url.getHost().toLowerCase();

        // Check against blocked hosts
        if (BLOCKED_HOSTS.contains(host)) {
            throw new SecurityException("Access to host is forbidden (SSRF protection): " + host);
        }

        // Check if host resolves to a private/internal IP
        try {
            InetAddress address = InetAddress.getByName(host);
            if (isPrivateOrReservedAddress(address)) {
                throw new SecurityException("Access to private/internal network addresses is forbidden (SSRF protection): " + host);
            }
        } catch (UnknownHostException e) {
            // Host cannot be resolved - could be legitimate or an attack
            // Log and allow (or throw based on security policy)
        }
    }

    private static boolean isPrivateOrReservedAddress(InetAddress address) {
        return address.isLoopbackAddress()
                || address.isSiteLocalAddress()
                || address.isLinkLocalAddress()
                || address.isAnyLocalAddress()
                || isCloudMetadataAddress(address);
    }

    private static boolean isCloudMetadataAddress(InetAddress address) {
        byte[] bytes = address.getAddress();
        // 169.254.169.254 (AWS/Azure/GCP metadata)
        if (bytes.length == 4) {
            return (bytes[0] & 0xFF) == 169
                    && (bytes[1] & 0xFF) == 254
                    && (bytes[2] & 0xFF) == 169
                    && (bytes[3] & 0xFF) == 254;
        }
        return false;
    }

    /**
     * Checks if a URL is safe without throwing an exception.
     *
     * @param urlString The URL string to check.
     * @return true if the URL is safe, false otherwise.
     */
    public static boolean isSafeUrl(String urlString) {
        try {
            validateUrl(urlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
