package com.modulythe.framework.infrastructure.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractR2dbcConverter.class);

    private final Function<? super A, ? extends C> toColumn;
    private final Map<C, A> values;
    private final Class<A> entityType;
    private final Class<C> columnType;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected AbstractR2dbcConverter(final Function<? super A, ? extends C> toColumn, final A... values) {
        this.toColumn = Objects.requireNonNull(toColumn);
        this.values = Arrays.stream(values)
                .collect(Collectors.toUnmodifiableMap(toColumn, Function.identity()));

        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(getClass(), AbstractR2dbcConverter.class);
        if (typeArguments != null && typeArguments.length == 2) {
            this.entityType = (Class<A>) typeArguments[0];
            this.columnType = (Class<C>) typeArguments[1];
        } else {
            LOGGER.warn("FAILED to resolve type for {} : ", getClass().getSimpleName());
            throw new IllegalStateException("Could not resolve generic type arguments for " + getClass().getName());
        }
    }

    /**
     * Returns the converter responsible for writing to the database (Entity -> Column).
     * <p>
     * This converter is intended for direct usage.
     * </p>
     *
     * @return a Converter instance
     */
    public Converter<A, C> getWritingConverter() {
        return new R2dbcWritingConverter();
    }

    /**
     * Returns the converter responsible for writing to the database (Entity -> Column).
     * <p>
     * This converter is intended for Spring Data registration (GenericConverter).
     * </p>
     *
     * @return a GenericConverter instance
     */
    public GenericConverter getWritingGenericConverter() {
        return new R2dbcWritingGenericConverter();
    }

    /**
     * Returns the converter responsible for reading from the database (Column -> Entity).
     * <p>
     * This converter is intended for direct usage.
     * </p>
     *
     * @return a Converter instance
     */
    public Converter<C, A> getReadingConverter() {
        return new R2dbcReadingConverter();
    }

    /**
     * Returns the converter responsible for reading from the database (Column -> Entity).
     * <p>
     * This converter is intended for Spring Data registration (GenericConverter).
     * </p>
     *
     * @return a GenericConverter instance
     */
    public GenericConverter getReadingGenericConverter() {
        return new R2dbcReadingGenericConverter();
    }

    @WritingConverter
    private class R2dbcWritingConverter implements Converter<A, C> {
        @Override
        public C convert(@Nullable A source) {
            if (source == null) return null;
            return toColumn.apply(source);
        }
    }

    @WritingConverter
    private class R2dbcWritingGenericConverter implements GenericConverter {
        @Override
        @Nullable
        public Set<ConvertiblePair> getConvertibleTypes() {
            if (entityType != null && columnType != null) {
                return Collections.singleton(new ConvertiblePair(entityType, columnType));
            }
            return null;
        }

        @Override
        @Nullable
        @SuppressWarnings("unchecked")
        public Object convert(@Nullable final Object source,
                              @NonNull TypeDescriptor sourceType,
                              @NonNull TypeDescriptor targetType) {
            if (source == null) return null;
            return toColumn.apply((A) source);
        }
    }

    @ReadingConverter
    private class R2dbcReadingConverter implements Converter<C, A> {
        @Override
        public A convert(@Nullable C source) {
            if (source == null) return null;
            A attribute = values.get(source);
            if (attribute == null) {
                throw new IllegalArgumentException("Unknown value: " + source);
            }
            return attribute;
        }
    }

    @ReadingConverter
    private class R2dbcReadingGenericConverter implements GenericConverter {
        @Override
        @Nullable
        public Set<ConvertiblePair> getConvertibleTypes() {
            if (entityType != null && columnType != null) {
                return Collections.singleton(new ConvertiblePair(columnType, entityType));
            }
            return null;
        }

        @Override
        @Nullable
        @SuppressWarnings("unchecked")
        public Object convert(@Nullable Object source,
                              @NonNull TypeDescriptor sourceType,
                              @NonNull TypeDescriptor targetType) {
            if (source == null) return null;
            C val = (C) source;
            A attribute = values.get(val);
            if (attribute == null) {
                throw new IllegalArgumentException("Unknown value: " + val);
            }
            return attribute;
        }
    }
}
