package com.modulythe.framework.domain.common.pagination;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents a filter for string values.
 * <p>
 * This filter is used to filter data based on exact string matches or substrings,
 * depending on the interpretation in the infrastructure layer.
 * </p>
 */
public final class FilterString extends Filter {

    public static FilterStringBuilder builder() {
        return new FilterStringBuilder();
    }

    public static final class FilterStringBuilder extends FilterBuilder<FilterString, FilterStringBuilder> {
        private String value;

        public FilterStringBuilder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        protected FilterStringBuilder self() {
            return this;
        }

        @Override
        protected FilterString buildFilter() {
            return new FilterString(this);
        }
    }

    @NotNull
    private final String value;

    private FilterString(FilterStringBuilder builder) {
        super(builder, Filter.FilterType.STRING);
        this.value = Objects.requireNonNull(builder.value, "Filter string value cannot be null");
        validate(this);
    }

    // Constructor for direct use in mapper (less ideal, but for quick fix)
    public FilterString(String name, String value) {
        super(new FilterStringBuilder().name(name).value(value), Filter.FilterType.STRING);
        this.value = Objects.requireNonNull(value, "Filter string value cannot be null");
        validate(this);
    }

    public String getValue() {
        return value;
    }
}
