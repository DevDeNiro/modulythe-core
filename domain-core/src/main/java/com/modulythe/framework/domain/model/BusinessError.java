package com.modulythe.framework.domain.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class BusinessError implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;
    private final String message;
    private final Map<String, String> additionalInfo;

    public BusinessError(String code, String message) {
        this(code, message, null);
    }

    public BusinessError(String code, String message, Map<String, String> additionalInfo) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Error code must not be blank");
        }
        if (StringUtils.isBlank(message)) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessError that = (BusinessError) o;
        return code.equals(that.code) &&
                message.equals(that.message) &&
                Objects.equals(additionalInfo, that.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, additionalInfo);
    }

    @Override
    public String toString() {
        return String.format("BusinessError[code='%s', message='%s', additionalInfo=%s]",
                code, message, additionalInfo);
    }

    /* Exemple d'utilisation

Dans un controller :

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Business validation failed");
        response.put("errors", ex.getBusinessErrors());
        return ResponseEntity.badRequest().body(response);
    }
}

     */
}