package com.modulythe.framework.domain.exception;

public class InvalidUniqueIdFormatException extends BusinessException {

    public InvalidUniqueIdFormatException(String message) {
        super(message, "UNIQUE_ID_INVALID_FORMAT");
    }
}
