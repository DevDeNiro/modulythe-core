package com.modulythe.framework.domain.common.pagination;


import com.modulythe.framework.domain.validation.Validate;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A container for complex filter values, including lists and ranges.
 * <p>
 * This class can be used to pass multiple types of filter criteria in a single object.
 * </p>
 */
public final class FilterValues implements Validate<FilterValues> {

    public static FilterValuesBuilder builder() {
        return new FilterValuesBuilder();
    }

    public static final class FilterValuesBuilder {
        private List<FilterListValues> filterListValuesList;
        private List<FilterRangeValues> filterRangeValuesList;

        private FilterValuesBuilder() {
        }

        private FilterValuesBuilder(FilterValues original) {
            this.filterListValuesList = original.filterListValuesList;
            this.filterRangeValuesList = original.filterRangeValuesList;
        }

        public FilterValuesBuilder filterListValuesList(List<FilterListValues> filterListValuesList) {
            this.filterListValuesList = filterListValuesList;
            return this;
        }

        public FilterValuesBuilder filterRangeValuesList(List<FilterRangeValues> filterRangeValuesList) {
            this.filterRangeValuesList = filterRangeValuesList;
            return this;
        }

        public FilterValues build() {
            return new FilterValues(this);
        }
    }

    @NotNull
    private final List<FilterListValues> filterListValuesList;

    @NotNull
    private final List<FilterRangeValues> filterRangeValuesList;

    private FilterValues(FilterValuesBuilder builder) {
        this.filterListValuesList = List.copyOf(
                builder.filterListValuesList == null ? Collections.emptyList() : builder.filterListValuesList
        );
        this.filterRangeValuesList = List.copyOf(
                builder.filterRangeValuesList == null ? Collections.emptyList() : builder.filterRangeValuesList
        );

        validate(this);
    }

    public FilterValuesBuilder toBuilder() {
        return new FilterValuesBuilder(this);
    }

    public List<FilterListValues> getFilterListValuesList() {
        return filterListValuesList;
    }

    public List<FilterRangeValues> getFilterRangeValuesList() {
        return filterRangeValuesList;
    }
}
