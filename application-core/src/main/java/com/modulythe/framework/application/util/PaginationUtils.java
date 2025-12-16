package com.modulythe.framework.application.util;

import com.modulythe.framework.domain.common.pagination.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for Controllers to simplify the creation of domain objects
 * related to pagination and filtering.
 */
public class PaginationUtils {

    private PaginationUtils() {
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
     * Creates a list of filters based on the provided value and type.
     * <p>
     * This method supports creating filters for STRING, BOOLEAN, and NUMBER types.
     * TODO: create specific builders for complex types (DATE, RANGE, LIST)
     * </p>
     *
     * @param filterKey   The key/name of the filter field.
     * @param filterValue The value of the filter.
     * @param type        The expected type of the filter.
     * @return A list containing the filter if valid, or an empty list.
     */
    public static List<Filter> createFilters(String filterKey, Object filterValue, Filter.FilterType type) {
        List<Filter> filters = new ArrayList<>();
        if (filterKey == null || filterValue == null) {
            return filters;
        }

        try {
            switch (type) {
                case STRING -> filters.add(new FilterString(filterKey, String.valueOf(filterValue)));
                case BOOLEAN -> {
                    if (filterValue instanceof Boolean b) {
                        filters.add(new FilterBoolean(filterKey, b));
                    } else {
                        filters.add(new FilterBoolean(filterKey, Boolean.parseBoolean(String.valueOf(filterValue))));
                    }
                }
                case NUMBER -> {
                    if (filterValue instanceof Number n) {
                        filters.add(new FilterNumber(filterKey, n.doubleValue()));
                    } else {
                        filters.add(new FilterNumber(filterKey, Double.parseDouble(String.valueOf(filterValue))));
                    }
                }
                default -> {
                    // Complex types are not supported yet
                }
            }
        } catch (IllegalArgumentException e) {
            // Ignore invalid values
        }
        return filters;
    }
}
