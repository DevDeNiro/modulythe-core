package com.modulythe.framework.infrastructure.common;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AttributeConverter Stub for Spring Data JPA
 *
 * @param <A> The entity attribute type
 * @param <C> The database column type
 */
public abstract class AbstractAttributeConverter<A, C> implements AttributeConverter<A, C> {

    private final Function<? super A, ? extends C> toColumn;
    private final Map<C, A> values;

    @SafeVarargs
    protected AbstractAttributeConverter(final Function<? super A, ? extends C> toColumn, final A... values) {
        this.toColumn = Objects.requireNonNull(toColumn);
        this.values = Arrays.stream(values)
                .collect(Collectors.toUnmodifiableMap(toColumn, Function.identity()));
    }

    @Override
    public C convertToDatabaseColumn(final A attribute) {
        if (attribute == null) {
            return null;
        }
        return toColumn.apply(attribute);
    }

    @Override
    public A convertToEntityAttribute(final C dbData) {
        if (dbData == null) {
            return null;
        }
        final A result = values.get(dbData);
        if (result == null) {
            throw new IllegalArgumentException("Unknown value: " + dbData);
        }
        return result;
    }
}