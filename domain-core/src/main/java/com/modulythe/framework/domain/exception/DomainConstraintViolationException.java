package com.modulythe.framework.domain.exception;

import jakarta.validation.ConstraintViolation;

import java.io.Serial;
import java.util.Set;

/**
 * Exception thrown when a domain object's validation constraints are violated.
 * This typically wraps a set of {@link jakarta.validation.ConstraintViolation} objects.
 */
public class DomainConstraintViolationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Set<ConstraintViolation<?>> constraintViolations;

    /**
     * Constructs a new exception with a detail message and the set of violations.
     *
     * @param message    The detail message.
     * @param violations The set of constraint violations.
     */
    public DomainConstraintViolationException(String message, Set<ConstraintViolation<?>> violations) {
        super(message);
        this.constraintViolations = violations;
    }

    /**
     * Returns the set of constraint violations that caused this exception.
     *
     * @return The set of {@link ConstraintViolation} objects.
     */
    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }
}
