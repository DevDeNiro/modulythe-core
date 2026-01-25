package com.modulythe.framework.domain.common.pagination;

/**
 * Represents a filter for a numeric range.
 * <p>
 * This filter allows filtering data where a value falls within a specified
 * minimum and maximum integer range.
 * </p>
 */
public final class FilterRange extends Filter {

    public static FilterRangeBuilder builder() {
        return new FilterRangeBuilder();
    }

    public static final class FilterRangeBuilder extends FilterBuilder<FilterRange, FilterRangeBuilder> {
        private int min;
        private int max;

        public FilterRangeBuilder min(int min) {
            this.min = min;
            return this;
        }

        public FilterRangeBuilder max(int max) {
            this.max = max;
            return this;
        }

        @Override
        protected FilterRangeBuilder self() {
            return this;
        }

        @Override
        protected FilterRange buildFilter() {
            return new FilterRange(this);
        }
    }

    private final int min;
    private final int max;

    private FilterRange(FilterRangeBuilder builder) {
        super(builder, Filter.FilterType.RANGE);
        this.min = builder.min;
        this.max = builder.max;
        validate(this);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
