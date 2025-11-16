package com.modulythe.framework.domain.common;


import com.modulythe.framework.domain.model.BusinessError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container for collecting business errors that occur during a domain operation.
 * This pattern allows methods to return a comprehensive list of all validation failures
 * instead of throwing an exception on the first error encountered.
 */
public class Notification {
    private final List<BusinessError> errors = new ArrayList<>();

    /**
     * Adds a new business error to the notification.
     *
     * @param code    A unique code identifying the error type.
     * @param message A human-readable message describing the error.
     */
    public void addError(String code, String message) {
        this.errors.add(new BusinessError(code, message));
    }

    /**
     * Checks if the notification contains any errors.
     *
     * @return {@code true} if there are one or more errors, {@code false} otherwise.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns an unmodifiable view of the list of errors.
     *
     * @return A list of {@link BusinessError} objects.
     */
    public List<BusinessError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "errors=" + errors +
                '}';
    }
}

