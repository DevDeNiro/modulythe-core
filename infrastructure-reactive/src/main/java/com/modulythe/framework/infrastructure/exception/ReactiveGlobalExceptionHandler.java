package com.modulythe.framework.infrastructure.exception;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Global exception handler for Reactive (WebFlux) applications.
 * <p>
 * In production mode, sensitive error details are hidden from clients and logged server-side.
 * A unique error reference ID (TraceId when available, otherwise UUID) is provided to clients for support purposes.
 * </p>
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveGlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveGlobalExceptionHandler.class);
    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please contact support with reference ID: ";

    private final Tracer tracer;

    @Value("${modulythe.exception.expose-details:false}")
    private boolean exposeDetails;

    public ReactiveGlobalExceptionHandler(@Autowired(required = false) @Nullable Tracer tracer) {
        this.tracer = tracer;
        if (tracer == null) {
            LOGGER.info("Micrometer Tracer not available - using UUID for error reference IDs");
        } else {
            LOGGER.info("Micrometer Tracer available - using TraceId for error reference IDs");
        }
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(@NonNull TechnicalException ex, @NonNull ServerWebExchange exchange) {
        String errorId = generateErrorId();
        String path = exchange.getRequest().getPath().value();
        LOGGER.error("[{}] TechnicalException at {}: {}", errorId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Technical Error",
                getSafeMessage(ex.getMessage(), errorId),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalFormedQueryException.class)
    public ResponseEntity<ErrorResponse> handleMalFormedQueryException(@NonNull MalFormedQueryException ex, @NonNull ServerWebExchange exchange) {
        String errorId = generateErrorId();
        String path = exchange.getRequest().getPath().value();
        LOGGER.warn("[{}] MalFormedQueryException at {}: {}", errorId, path, ex.getMessage());

        // MalFormedQueryException messages are generally safe to expose as they describe query syntax issues
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "MalFormed Query",
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(@NonNull IllegalArgumentException ex, @NonNull ServerWebExchange exchange) {
        String errorId = generateErrorId();
        String path = exchange.getRequest().getPath().value();
        LOGGER.warn("[{}] IllegalArgumentException at {}: {}", errorId, path, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                getSafeMessage(ex.getMessage(), errorId),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(@NonNull SecurityException ex, @NonNull ServerWebExchange exchange) {
        String errorId = generateErrorId();
        String path = exchange.getRequest().getPath().value();
        LOGGER.error("[{}] SecurityException at {}: {}", errorId, path, ex.getMessage(), ex);

        // Never expose security exception details
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "Access denied. Reference ID: " + errorId,
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(@NonNull Exception ex, @NonNull ServerWebExchange exchange) {
        String errorId = generateErrorId();
        String path = exchange.getRequest().getPath().value();
        LOGGER.error("[{}] Unexpected exception at {}: {}", errorId, path, ex.getMessage(), ex);

        // Never expose raw exception messages for generic exceptions in production
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected Error",
                exposeDetails ? ex.getMessage() : GENERIC_ERROR_MESSAGE + errorId,
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns the exception message if details are exposed, otherwise a generic message with error ID.
     */
    private String getSafeMessage(String originalMessage, String errorId) {
        if (exposeDetails) {
            return originalMessage;
        }
        return GENERIC_ERROR_MESSAGE + errorId;
    }

    /**
     * Generates a unique error reference ID for tracking purposes.
     * Uses TraceId from Micrometer Tracing if available, otherwise falls back to UUID.
     */
    private String generateErrorId() {
        if (tracer != null) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                return currentSpan.context().traceId();
            }
        }
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
