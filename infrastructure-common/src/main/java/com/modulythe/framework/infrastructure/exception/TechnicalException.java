package com.modulythe.framework.infrastructure.exception;

import java.io.Serial;

/**
 * Represents a technical exception that occurs due to system-level errors or infrastructure issues.
 * This exception is unchecked, as it is generally unrecoverable and requires logging or escalation.
 */
public class TechnicalException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TechnicalException with a message.
     *
     * @param message the error message.
     */
    public TechnicalException(String message) {
        super(validateMessage(message));
    }

    /**
     * Constructs a TechnicalException with a message and cause.
     *
     * @param message the error message.
     * @param cause   the root cause of the exception.
     */
    public TechnicalException(String message, Throwable cause) {
        super(validateMessage(message), cause);
    }

    /**
     * Constructs a TechnicalException with advanced options for suppression and stack trace writability.
     *
     * @param message            the error message.
     * @param cause              the root cause of the exception.
     * @param enableSuppression  whether suppression is enabled or disabled.
     * @param writableStackTrace whether the stack trace should be writable.
     */
    public TechnicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(validateMessage(message), cause, enableSuppression, writableStackTrace);
    }

    /**
     * Validates that the message is not null or blank.
     *
     * @param message the error message.
     * @return the validated message.
     */
    private static String validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Error message must not be null or blank");
        }
        return message;
    }
}
