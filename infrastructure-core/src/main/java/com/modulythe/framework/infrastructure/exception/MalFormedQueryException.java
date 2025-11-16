package com.modulythe.framework.infrastructure.exception;

import java.io.Serial;

public class MalFormedQueryException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MalFormedQueryException(String message) {
        super(message);
    }
}
