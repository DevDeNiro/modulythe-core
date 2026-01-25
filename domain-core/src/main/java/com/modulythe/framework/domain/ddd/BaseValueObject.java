package com.modulythe.framework.domain.ddd;

import com.modulythe.framework.domain.validation.Validate;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Base class for immutable Value Objects
 *
 * @param <T> The concret type of the ValueObject
 */
public abstract class BaseValueObject<T extends BaseValueObject<T>> implements Validate<T>, Serializable {
    private final Class<T> type;
    private volatile int cachedHashCode = -1;

    protected BaseValueObject(Class<T> type) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }

    /**
     * List of attributes participating in equals/hashCode.
     */
    protected abstract List<Object> attributesToIncludeInEqualityCheck();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!type.isInstance(obj)) return false;
        T other = type.cast(obj);
        return attributesToIncludeInEqualityCheck().equals(
                other.attributesToIncludeInEqualityCheck()
        );
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == -1) {
            synchronized (this) {
                if (cachedHashCode == -1) {
                    cachedHashCode = Objects.hash(attributesToIncludeInEqualityCheck().toArray());
                }
            }
        }
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s]", type.getSimpleName(), attributesToIncludeInEqualityCheck());
    }
}
