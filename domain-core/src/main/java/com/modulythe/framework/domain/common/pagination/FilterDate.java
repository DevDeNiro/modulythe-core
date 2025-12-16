package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.exception.DomainConstraintViolationException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Collections.emptySet;

/**
 * Represents a filter for date values.
 * <p>
 * This filter supports filtering by a date range (BETWEEN), or by a date being
 * after or before a specific date.
 * </p>
 */
public final class FilterDate extends Filter {

    public enum FilterDateType {
        BETWEEN,
        AFTER,
        BEFORE
    }

    public static FilterDateBuilder builder() {
        return new FilterDateBuilder();
    }

    public static final class FilterDateBuilder extends FilterBuilder<FilterDate, FilterDateBuilder> {
        private LocalDate startDate;
        private LocalDate endDate;
        private FilterDateType filterDateType;

        public FilterDateBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public FilterDateBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public FilterDateBuilder filterDateType(FilterDateType filterDateType) {
            this.filterDateType = filterDateType;
            return this;
        }

        @Override
        protected FilterDateBuilder self() {
            return this;
        }

        @Override
        protected FilterDate buildFilter() {
            return new FilterDate(this);
        }
    }

    private final LocalDate startDate;
    private final LocalDate endDate;

    @NotNull
    private final FilterDateType filterDateType;

    private FilterDate(FilterDateBuilder builder) {
        super(builder, Filter.FilterType.DATE);
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.filterDateType = builder.filterDateType;
        validateInputValues();
        validate(this);
    }

    private void validateInputValues() {
        switch (filterDateType) {
            case BETWEEN -> {
                if (startDate == null || endDate == null) {
                    throw new DomainConstraintViolationException(
                            "Start date and end date must be provided for BETWEEN filter date type",
                            emptySet()
                    );
                }
                if (startDate.isAfter(endDate)) {
                    throw new DomainConstraintViolationException(
                            "Start date must be before or equal to end date",
                            emptySet()
                    );
                }
            }
            case AFTER -> {
                if (startDate == null) {
                    throw new DomainConstraintViolationException(
                            "Start date must be provided for AFTER filter date type",
                            emptySet()
                    );
                }
            }
            case BEFORE -> {
                if (endDate == null) {
                    throw new DomainConstraintViolationException(
                            "End date must be provided for BEFORE filter date type",
                            emptySet()
                    );
                }
            }
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public FilterDateType getFilterDateType() {
        return filterDateType;
    }

    public static FilterDateType parseType(String name) {
        return Arrays.stream(FilterDateType.values())
                .filter(filterDateType -> filterDateType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown filter date type: " + name));
    }
}
