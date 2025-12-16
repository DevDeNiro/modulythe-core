package com.modulythe.framework.domain.common.pagination;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents a filter for numeric values.
 * <p>
 * This filter is used to filter data based on an exact numeric match.
 * </p>
 */
public final class FilterNumber extends Filter {

    public static FilterNumberBuilder builder() {
        return new FilterNumberBuilder();
    }

    public static final class FilterNumberBuilder extends FilterBuilder<FilterNumber, FilterNumberBuilder> {
        private Double value;

        public FilterNumberBuilder value(Double value) {
            this.value = value;
            return this;
        }

        @Override
        protected FilterNumberBuilder self() {
            return this;
        }

        @Override
        protected FilterNumber buildFilter() {
            return new FilterNumber(this);
        }
    }

    @NotNull
    private final Double value;

    private FilterNumber(FilterNumberBuilder builder) {
        super(builder, Filter.FilterType.NUMBER);
        this.value = Objects.requireNonNull(builder.value, "Filter number value cannot be null");
        validate(this);
    }

    // Constructor for direct use in mapper (less ideal, but for quick fix)
    public FilterNumber(String name, Double value) {
        super(new FilterNumberBuilder().name(name).value(value), Filter.FilterType.NUMBER);
        this.value = Objects.requireNonNull(value, "Filter number value cannot be null");
        validate(this);
    }

    public Double getValue() {
        return value;
    }
}
