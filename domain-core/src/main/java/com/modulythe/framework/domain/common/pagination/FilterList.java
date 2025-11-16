package com.modulythe.framework.domain.common.pagination;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public final class FilterList extends Filter {

    public static FilterListBuilder builder() {
        return new FilterListBuilder();
    }

    public static final class FilterListBuilder extends FilterBuilder<FilterList, FilterListBuilder> {
        private FilterListValues values;

        public FilterListBuilder values(FilterListValues values) {
            this.values = values;
            return this;
        }

        @Override
        protected FilterListBuilder self() {
            return this;
        }

        @Override
        protected FilterList buildFilter() {
            return new FilterList(this);
        }
    }

    @NotNull
    private final FilterListValues values;

    private FilterList(FilterListBuilder builder) {
        super(builder, Filter.FilterType.LIST);
        this.values = Objects.requireNonNull(builder.values, "Filter list values cannot be null");
        validate(this);
    }

    // Constructor for direct use in mapper (less ideal, but for quick fix)
    public FilterList(String name, FilterListValues values) {
        super(new FilterListBuilder().name(name).values(values), Filter.FilterType.LIST);
        this.values = Objects.requireNonNull(values, "Filter list values cannot be null");
        validate(this);
    }

    public FilterListValues getValues() {
        return values;
    }
}
