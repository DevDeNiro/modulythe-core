package com.modulythe.framework.domain.exception;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents an exception that occurs due to business rule violations in the domain.
 * This exception is checked to ensure explicit handling of business errors.
 * Must be checked exception for DDD compliance, but it's complicated to apply due to mapping dto <-> domain model <-> entity Jpa
 */
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<BusinessError> businessErrors;

    /**
     * Constructs a BusinessException with a single error message and code.
     *
     * @param message the business error message.
     * @param code    the business error code.
     */
    public BusinessException(String message, String code) {
        super(message);
        this.businessErrors = Collections.singletonList(new BusinessError(code, message));
    }

    /**
     * Constructs a BusinessException with a single error message, code, and cause.
     *
     * @param message the business error message.
     * @param code    the business error code.
     * @param cause   the cause of the exception.
     */
    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
        this.businessErrors = Collections.singletonList(new BusinessError(code, message));
    }

    /**
     * Constructs a BusinessException with additional information.
     *
     * @param message        the business error message.
     * @param code           the business error code.
     * @param additionalInfo additional context about the error in key-value format.
     */
    public BusinessException(String message, String code, Map<String, String> additionalInfo) {
        super(message);
        this.businessErrors = Collections.singletonList(new BusinessError(code, message, additionalInfo));
    }

    /**
     * Constructs a BusinessException with a list of BusinessError objects.
     *
     * @param businessErrors the list of business errors.
     */
    public BusinessException(List<BusinessError> businessErrors) {
        super(messageFromBusinessErrors(businessErrors));
        if (businessErrors == null || businessErrors.isEmpty()) {
            throw new IllegalArgumentException("Business errors must not be null or empty");
        }
        this.businessErrors = List.copyOf(businessErrors);
    }

    /**
     * Generates a concatenated message from a list of BusinessError objects.
     *
     * @param businessErrors the list of business errors.
     * @return a concatenated error message string.
     */
    private static String messageFromBusinessErrors(List<BusinessError> businessErrors) {
        return businessErrors.stream()
                .map(BusinessError::getMessage)
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Unknown business error");
    }

    /**
     * Gets the list of BusinessError objects associated with this exception.
     *
     * @return the list of business errors.
     */
    public List<BusinessError> getBusinessErrors() {
        return businessErrors;
    }

    /**
     * Represents an individual business error with a code, message, and optional additional information.
     */
    public static class BusinessError implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String code;
        private final String message;
        private final Map<String, String> additionalInfo;

        public BusinessError(String code, String message) {
            this(code, message, null);
        }

        public BusinessError(String code, String message, Map<String, String> additionalInfo) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Error code must not be blank");
            }
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Error message must not be blank");
            }
            this.code = code;
            this.message = message;
            this.additionalInfo = additionalInfo != null
                    ? Collections.unmodifiableMap(additionalInfo)
                    : Collections.emptyMap();
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, String> getAdditionalInfo() {
            return additionalInfo;
        }

        @Override
        public String toString() {
            return "BusinessError{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", additionalInfo=" + additionalInfo +
                    '}';
        }
    }
}