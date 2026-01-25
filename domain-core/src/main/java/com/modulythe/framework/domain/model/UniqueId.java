package com.modulythe.framework.domain.model;

import com.modulythe.framework.domain.ddd.BaseValueObject;
import com.modulythe.framework.domain.exception.BusinessException;
import com.modulythe.framework.domain.exception.InvalidUniqueIdFormatException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique identifier (UUID) in the domain.
 * <p>
 * Recommended usage:
 * <p>
 * UniqueId userId = UniqueId.generate(); <br>
 * UniqueId existingId = UniqueId.of(UUID_AS_STRING);
 */
@SuppressWarnings("java:S2160") // "false positive"
public final class UniqueId extends BaseValueObject<UniqueId> implements Serializable {

    @ValidUniqueId
    private final String value;

    /**
     * Private constructor used by the static generate() method to create a random UUID.
     */
    private UniqueId() {
        super(UniqueId.class);
        this.value = UUID.randomUUID().toString();
    }

    /**
     * Private constructor to build a UniqueId from a known value.
     */
    private UniqueId(String value) {
        super(UniqueId.class);
        if (StringUtils.isBlank(value)) {
            throw new BusinessException("UniqueId cannot be empty", "UNIQUE_ID_EMPTY");
        }
        if (value.length() > 36) {
            throw new BusinessException("UniqueId must be at most 36 characters (UUID format)", "UNIQUE_ID_TOO_LONG");
        }
        // Simple validation of UUID format: throws an exception if invalid
        validateUuidFormat(value);
        this.value = value;
    }

    /**
     * Constructeur de copie.
     */
    public UniqueId(UniqueId other) {
        this(Objects.requireNonNull(other, "Other UniqueId cannot be null").value);
    }

    /**
     * Static factory method to create a UniqueId from a known value (UUID as a String).
     *
     * @param rawValue the raw value of the UniqueId
     * @return an immutable UniqueId
     */
    public static UniqueId of(String rawValue) {
        return new UniqueId(rawValue);
    }

    /**
     * Static factory method to generate a new random UniqueId.
     *
     * @return a new immutable UniqueId
     */
    public static UniqueId generate() {
        return new UniqueId();
    }

    public String getValue() {
        return this.value;
    }

    /**
     * Validate the UUID format.
     */
    private static void validateUuidFormat(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidUniqueIdFormatException("Invalid UUID format: " + value);
        }
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return Collections.singletonList(this.value);
    }

    /**
     * Custom validation annotation for UniqueId.
     */
    @Documented
    @NotEmpty(message = "UniqueId cannot be empty")
    @Size(max = 36, message = "UniqueId must be at most 36 characters (UUID format)")
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidUniqueId {
        String message() default "Unique Id must be a valid UUID of 36 characters";

        Class<?>[] groups() default {};

        Class<? extends jakarta.validation.Payload>[] payload() default {};
    }
}