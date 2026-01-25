package com.modulythe.framework.infrastructure.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Global exception handler for Servlet-based applications.
 * <p>
 * In production mode, sensitive error details are hidden from clients and logged server-side.
 * A unique error reference ID is provided to clients for support purposes.
 * </p>
 */
@Validated
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletGlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletGlobalExceptionHandler.class);
    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please contact support with reference ID: ";

    @Value("${modulythe.exception.expose-details:false}")
    private boolean exposeDetails;

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(@NonNull TechnicalException ex, @NonNull WebRequest request) {
        String errorId = generateErrorId();
        LOGGER.error("[{}] TechnicalException at {}: {}", errorId, request.getDescription(false), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Technical Error",
                getSafeMessage(ex.getMessage(), errorId),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalFormedQueryException.class)
    public ResponseEntity<ErrorResponse> handleMalFormedQueryException(@NonNull MalFormedQueryException ex, @NonNull WebRequest request) {
        String errorId = generateErrorId();
        LOGGER.warn("[{}] MalFormedQueryException at {}: {}", errorId, request.getDescription(false), ex.getMessage());

        // MalFormedQueryException messages are generally safe to expose as they describe query syntax issues
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "MalFormed Query",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(@NonNull IllegalArgumentException ex, @NonNull WebRequest request) {
        String errorId = generateErrorId();
        LOGGER.warn("[{}] IllegalArgumentException at {}: {}", errorId, request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                getSafeMessage(ex.getMessage(), errorId),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(@NonNull SecurityException ex, @NonNull WebRequest request) {
        String errorId = generateErrorId();
        LOGGER.error("[{}] SecurityException at {}: {}", errorId, request.getDescription(false), ex.getMessage(), ex);

        // Never expose security exception details
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "Access denied. Reference ID: " + errorId,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(@NonNull Exception ex, @NonNull WebRequest request) {
        String errorId = generateErrorId();
        LOGGER.error("[{}] Unexpected exception at {}: {}", errorId, request.getDescription(false), ex.getMessage(), ex);

        // Never expose raw exception messages for generic exceptions in production
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected Error",
                exposeDetails ? ex.getMessage() : GENERIC_ERROR_MESSAGE + errorId,
                request.getDescription(false)
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
     */
    private String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
