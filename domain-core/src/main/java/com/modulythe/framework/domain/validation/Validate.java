package com.modulythe.framework.domain.validation;

import com.modulythe.framework.domain.exception.DomainConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Interface to add JSR-303 validation to a class.
 */
public interface Validate<T> {
    default Validator getValidator() {
        return JsrValidatorProvider.getValidator();
    }

    default void validate(T object) {
        if (object == null) {
            throw new DomainConstraintViolationException("Object cannot be null", emptySet());
        }
        Set<ConstraintViolation<T>> violations = getValidator().validate(object);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Validation failed for %s:%n", object.getClass().getTypeName()));
            for (ConstraintViolation<T> v : violations) {
                sb.append(String.format(" - %s: %s (invalid value: %s)%n",
                        v.getPropertyPath(), v.getMessage(), v.getInvalidValue()));
            }
            throw new DomainConstraintViolationException(sb.toString(), new HashSet<>(violations));
        }
    }
}
