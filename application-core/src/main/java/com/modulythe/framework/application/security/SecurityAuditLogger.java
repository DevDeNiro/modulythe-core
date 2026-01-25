package com.modulythe.framework.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Audit logger for security-related events.
 * <p>
 * This component provides structured logging for authentication, authorization,
 * and other security events for compliance and forensic purposes.
 * </p>
 */
@Component
public class SecurityAuditLogger {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("SECURITY_AUDIT");

    private static final String EVENT_TYPE = "eventType";
    private static final String USER_ID = "userId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String TIMESTAMP = "timestamp";

    /**
     * Security event types for audit logging.
     */
    public enum SecurityEventType {
        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        AUTHORIZATION_FAILURE,
        TOKEN_REFRESH_SUCCESS,
        TOKEN_REFRESH_FAILURE,
        INVALID_TOKEN,
        SESSION_CREATED,
        SESSION_DESTROYED,
        SUSPICIOUS_ACTIVITY,
        SSRF_ATTEMPT,
        RATE_LIMIT_EXCEEDED
    }

    /**
     * Logs a security event with the given details.
     *
     * @param eventType The type of security event.
     * @param userId    The user ID (or "anonymous" if not authenticated).
     * @param ipAddress The client IP address.
     * @param details   Additional details about the event.
     */
    public void logSecurityEvent(SecurityEventType eventType, String userId, String ipAddress, String details) {
        try {
            MDC.put(EVENT_TYPE, eventType.name());
            MDC.put(USER_ID, userId != null ? userId : "anonymous");
            MDC.put(IP_ADDRESS, ipAddress != null ? ipAddress : "unknown");
            MDC.put(TIMESTAMP, Instant.now().toString());

            String message = String.format("[%s] User: %s, IP: %s - %s",
                    eventType.name(),
                    userId != null ? userId : "anonymous",
                    ipAddress != null ? ipAddress : "unknown",
                    details != null ? details : "");

            switch (eventType) {
                case AUTHENTICATION_FAILURE, AUTHORIZATION_FAILURE, INVALID_TOKEN, SUSPICIOUS_ACTIVITY, SSRF_ATTEMPT ->
                        AUDIT_LOGGER.warn(message);
                case RATE_LIMIT_EXCEEDED ->
                        AUDIT_LOGGER.error(message);
                default ->
                        AUDIT_LOGGER.info(message);
            }
        } finally {
            MDC.remove(EVENT_TYPE);
            MDC.remove(USER_ID);
            MDC.remove(IP_ADDRESS);
            MDC.remove(TIMESTAMP);
        }
    }

    /**
     * Logs a security event with additional metadata.
     *
     * @param eventType The type of security event.
     * @param userId    The user ID.
     * @param ipAddress The client IP address.
     * @param details   Event details.
     * @param metadata  Additional key-value metadata.
     */
    public void logSecurityEvent(SecurityEventType eventType, String userId, String ipAddress,
                                 String details, Map<String, String> metadata) {
        try {
            if (metadata != null) {
                metadata.forEach(MDC::put);
            }
            logSecurityEvent(eventType, userId, ipAddress, details);
        } finally {
            if (metadata != null) {
                metadata.keySet().forEach(MDC::remove);
            }
        }
    }

    /**
     * Logs a successful authentication event.
     */
    public void logAuthenticationSuccess(String userId, String ipAddress) {
        logSecurityEvent(SecurityEventType.AUTHENTICATION_SUCCESS, userId, ipAddress, "Authentication successful");
    }

    /**
     * Logs a failed authentication event.
     */
    public void logAuthenticationFailure(String userId, String ipAddress, String reason) {
        logSecurityEvent(SecurityEventType.AUTHENTICATION_FAILURE, userId, ipAddress, "Authentication failed: " + reason);
    }

    /**
     * Logs an authorization failure event.
     */
    public void logAuthorizationFailure(String userId, String ipAddress, String resource) {
        logSecurityEvent(SecurityEventType.AUTHORIZATION_FAILURE, userId, ipAddress,
                "Authorization denied for resource: " + resource);
    }

    /**
     * Logs a suspicious activity event.
     */
    public void logSuspiciousActivity(String userId, String ipAddress, String activity) {
        logSecurityEvent(SecurityEventType.SUSPICIOUS_ACTIVITY, userId, ipAddress,
                "Suspicious activity detected: " + activity);
    }

    /**
     * Logs an SSRF attempt event.
     */
    public void logSsrfAttempt(String ipAddress, String targetUrl) {
        logSecurityEvent(SecurityEventType.SSRF_ATTEMPT, null, ipAddress,
                "SSRF attempt blocked for URL: " + targetUrl);
    }
}
