package com.modulythe.framework.infrastructure.common;

import com.modulythe.framework.infrastructure.exception.TechnicalException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AttributeConverter Stub for Spring Data R2DBC.
 * <p>
 * This class provides a mechanism to define a pair of converters (Reading and Writing)
 * </p>
 *
 * @param <A> The entity attribute type
 * @param <C> The database column type
 */
public abstract class AbstractR2dbcConverter<A, C> {

    private final Function<? super A, ? extends C> toColumn;
    private final Map<C, A> values;

    @SafeVarargs
    protected AbstractR2dbcConverter(final Function<? super A, ? extends C> toColumn, final A... values) {
        this.toColumn = Objects.requireNonNull(toColumn);
        this.values = Arrays.stream(values)
                .collect(Collectors.toUnmodifiableMap(toColumn, Function.identity()));
    }

    /**
     * Returns the converter responsible for writing to the database (Entity -> Column).
     *
     * @return a Converter instance
     */
    public Converter<A, C> getWritingConverter() {
        return new R2dbcWritingConverter();
    }

    /**
     * Returns the converter responsible for reading from the database (Column -> Entity).
     *
     * @return a Converter instance
     */
    public Converter<C, A> getReadingConverter() {
        return new R2dbcReadingConverter();
    }

    @WritingConverter
    private class R2dbcWritingConverter implements Converter<A, C> {
        @Override
        public C convert(A source) {
            // Spring Data converters are typically invoked with non-null values,
            // but we include a check for safety and consistency.
            if (source == null) {
                return null;
            }
            return toColumn.apply(source);
        }
    }

    @ReadingConverter
    private class R2dbcReadingConverter implements Converter<C, A> {
        @Override
        public A convert(C source) {
            if (source == null) {
                return null;
            }
            A attribute = values.get(source);
            if (attribute == null) {
                throw new IllegalArgumentException("Unknown value: " + source);
            }
            return attribute;
        }
    }
}
