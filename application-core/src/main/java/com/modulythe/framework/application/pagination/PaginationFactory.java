package com.modulythe.framework.application.pagination;

import com.modulythe.framework.domain.common.pagination.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class for Controllers to simplify the creation of domain objects
 * related to pagination and filtering.
 */
public class PaginationFactory {

    private PaginationFactory() {
        // Utility class
    }

    /**
     * Creates a {@link PageableModel} from pagination parameters.
     * Handles 1-based page index conversion to 0-based.
     *
     * @param page          The page number (1-based).
     * @param size          The number of items per page.
     * @param sortColumn    The column name to sort by.
     * @param sortDirection The direction of the sort (ASC or DESC).
     * @return A configured {@link PageableModel}.
     */
    public static PageableModel createPageable(int page, int size, String sortColumn, SortModel.Direction sortDirection) {
        SortModel.Order order = SortModel.Order.by(sortColumn, sortDirection);
        SortModel sort = SortModel.by(Collections.singletonList(order));
        int pageIndex = (page < 1) ? 0 : page - 1;
        return PageableModel.of(pageIndex, size, sort);
    }

    /**
     * Creates a {@link PageableModel} with default sorting (sentAt DESC).
     *
     * @param page The page number (1-based).
     * @param size The number of items per page.
     * @return A configured {@link PageableModel}.
     */
    public static PageableModel createPageable(int page, int size) {
        return createPageable(page, size, "sentAt", SortModel.Direction.DESC);
    }

    /**
     * Creates a list of filters containing a single string filter if the value differs from the default.
     *
     * @param filterKey          The key/name of the filter field.
     * @param filterValue        The value of the filter.
     * @param defaultFilterValue The value considered as "no filter" (e.g. "ALL").
     * @return A list containing the filter if valid, or an empty list.
     */
    public static List<Filter> createFilters(String filterKey, String filterValue, String defaultFilterValue) {
        List<Filter> filters = new ArrayList<>();
        if (filterKey != null && filterValue != null && !filterValue.equals(defaultFilterValue)) {
            filters.add(new FilterString(filterKey, filterValue));
        }
        return filters;
    }

    /**
     * Creates a list of filters with "ALL" as the default ignored value.
     *
     * @param filterKey   The key/name of the filter field.
     * @param filterValue The value of the filter.
     * @return A list containing the filter if valid, or an empty list.
     */
    public static List<Filter> createFilters(String filterKey, String filterValue) {
        return createFilters(filterKey, filterValue, "ALL");
    }

    /**
     * Creates a single filter based on the provided value and type.
     * <p>
     * This method supports creating filters for STRING, BOOLEAN, NUMBER, and LIST types.
     * </p>
     *
     * @param filterKey   The key/name of the filter field.
     * @param filterValue The value of the filter.
     * @param type        The expected type of the filter.
     * @return The created filter, or null if the value is invalid or null.
     */
    public static Filter createFilter(String filterKey, Object filterValue, Filter.FilterType type) {
        if (filterKey == null || filterValue == null) {
            return null;
        }

        try {
            switch (type) {
                case STRING -> {
                    return new FilterString(filterKey, String.valueOf(filterValue));
                }
                case BOOLEAN -> {
                    if (filterValue instanceof Boolean b) {
                        return new FilterBoolean(filterKey, b);
                    } else {
                        return new FilterBoolean(filterKey, Boolean.parseBoolean(String.valueOf(filterValue)));
                    }
                }
                case NUMBER -> {
                    if (filterValue instanceof Number n) {
                        return new FilterNumber(filterKey, n.doubleValue());
                    } else {
                        return new FilterNumber(filterKey, Double.parseDouble(String.valueOf(filterValue)));
                    }
                }
                case LIST -> {
                    if (filterValue instanceof List<?> list) {
                        List<String> stringValues = list.stream()
                                .map(String::valueOf)
                                .toList();
                        return new FilterList(filterKey, new FilterListValues(stringValues));
                    } else if (filterValue instanceof FilterListValues values) {
                        return new FilterList(filterKey, values);
                    }
                }
                default -> {
                    // Complex types are not supported yet
                }
            }
        } catch (IllegalArgumentException e) {
            // Ignore invalid values
        }
        return null;
    }

    /**
     * Creates a list of filters based on the provided value and type.
     * <p>
     * This method supports creating filters for STRING, BOOLEAN, NUMBER, and LIST types.
     * </p>
     *
     * @param filterKey   The key/name of the filter field.
     * @param filterValue The value of the filter.
     * @param type        The expected type of the filter.
     * @return A list containing the filter if valid, or an empty list.
     */
    public static List<Filter> createFilters(String filterKey, Object filterValue, Filter.FilterType type) {
        List<Filter> filters = new ArrayList<>();
        Filter filter = createFilter(filterKey, filterValue, type);
        if (filter != null) {
            filters.add(filter);
        }
        return filters;
    }
    /**
     * Creates a specific filter for Date range (BETWEEN).
     *
     * @param filterKey The key/name of the filter field.
     * @param start     The start date (inclusive).
     * @param end       The end date (inclusive).
     * @return A {@link FilterDate} configured as BETWEEN.
     */
    public static Filter createDateFilter(String filterKey, LocalDate start, LocalDate end) {
        if (filterKey == null || start == null || end == null) {
            return null;
        }
        return FilterDate.builder()
                .name(filterKey)
                .startDate(start)
                .endDate(end)
                .filterDateType(FilterDate.FilterDateType.BETWEEN)
                .build();
    }

    /**
     * Creates a specific filter for Date (AFTER or BEFORE).
     *
     * @param filterKey The key/name of the filter field.
     * @param date      The date value.
     * @param type      The type of comparison (AFTER or BEFORE).
     * @return A {@link FilterDate} configured as requested.
     * @throws IllegalArgumentException if the type is BETWEEN (use the 3-arg method instead).
     */
    public static Filter createDateFilter(String filterKey, LocalDate date, FilterDate.FilterDateType type) {
        if (filterKey == null || date == null || type == null) {
            return null;
        }
        if (type == FilterDate.FilterDateType.BETWEEN) {
            throw new IllegalArgumentException("For BETWEEN type, please use createDateFilter(key, start, end)");
        }

        FilterDate.FilterDateBuilder builder = FilterDate.builder()
                .name(filterKey)
                .filterDateType(type);

        if (type == FilterDate.FilterDateType.AFTER) {
            builder.startDate(date);
        } else {
            builder.endDate(date);
        }

        return builder.build();
    }

    /**
     * Creates a specific filter for numeric Range.
     *
     * @param filterKey The key/name of the filter field.
     * @param min       The minimum value (inclusive).
     * @param max       The maximum value (inclusive).
     * @return A {@link FilterRange} configured with min and max.
     */
    public static Filter createRangeFilter(String filterKey, int min, int max) {
        if (filterKey == null) {
            return null;
        }
        return FilterRange.builder()
                .name(filterKey)
                .min(min)
                .max(max)
                .build();
    }

    /**
     * Creates a specific filter for a List of strings.
     *
     * @param filterKey The key/name of the filter field.
     * @param values    The list of allowed values.
     * @return A {@link FilterList} containing the provided values.
     */
    public static Filter createListFilter(String filterKey, List<String> values) {
        if (filterKey == null || values == null || values.isEmpty()) {
            return null;
        }
        return new FilterList(filterKey, new FilterListValues(values));
    }
}
