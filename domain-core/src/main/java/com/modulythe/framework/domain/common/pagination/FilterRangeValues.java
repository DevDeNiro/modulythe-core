package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.ddd.BaseValueObject;

import java.util.List;

/**
 * Value object representing a range of double values.
 * <p>
 * Used to define a min and max value for range-based filtering, potentially
 * used in other composite filters.
 * </p>
 */
@SuppressWarnings("java:S2160") // "false positive"
public final class FilterRangeValues extends BaseValueObject<FilterRangeValues> {
    private final double min;
    private final double max;

    public static FilterValuesRangeBuilder builder() {
        return new FilterValuesRangeBuilder();
    }

    public static final class FilterValuesRangeBuilder {
        private double min;
        private double max;

        public FilterValuesRangeBuilder min(double min) {
            this.min = min;
            return this;
        }

        public FilterValuesRangeBuilder max(double max) {
            this.max = max;
            return this;
        }

        public FilterRangeValues build() {
            return new FilterRangeValues(this.min, this.max);
        }
    }

    public FilterRangeValues(double min, double max) {
        super(FilterRangeValues.class);
        this.min = min;
        this.max = max;
        validate(this);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return List.of(min, max);
    }
}
