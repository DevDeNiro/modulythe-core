package com.modulythe.framework.domain.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Provider for a singleton Validator instance.
 */
public final class JsrValidatorProvider {
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    private JsrValidatorProvider() {
    }

    public static Validator getValidator() {
        return FACTORY.getValidator();
    }
}
