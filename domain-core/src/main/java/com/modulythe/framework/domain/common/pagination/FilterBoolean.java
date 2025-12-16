package com.modulythe.framework.domain.common.pagination;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents a filter for boolean values.
 * <p>
 * This filter allows filtering data based on a true/false condition.
 * </p>
 */
public final class FilterBoolean extends Filter {

    public static FilterBooleanBuilder builder() {
        return new FilterBooleanBuilder();
    }

    public static final class FilterBooleanBuilder extends FilterBuilder<FilterBoolean, FilterBooleanBuilder> {
        private Boolean value;

        public FilterBooleanBuilder value(Boolean value) {
            this.value = value;
            return this;
        }

        @Override
        protected FilterBooleanBuilder self() {
            return this;
        }

        @Override
        protected FilterBoolean buildFilter() {
            return new FilterBoolean(this);
        }
    }

    @NotNull
    private final Boolean value;

    private FilterBoolean(FilterBooleanBuilder builder) {
        super(builder, Filter.FilterType.BOOLEAN);
        this.value = Objects.requireNonNull(builder.value, "Filter boolean value cannot be null");
        validate(this);
    }

    // Constructor for direct use in mapper (less ideal, but for quick fix)
    public FilterBoolean(String name, Boolean value) {
        super(new FilterBooleanBuilder().name(name).value(value), Filter.FilterType.BOOLEAN);
        this.value = Objects.requireNonNull(value, "Filter boolean value cannot be null");
        validate(this);
    }

    public Boolean getValue() {
        return value;
    }
}
