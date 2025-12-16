package com.modulythe.framework.presentation.util;

import com.modulythe.framework.domain.common.pagination.Filter;
import com.modulythe.framework.domain.common.pagination.FilterString;
import com.modulythe.framework.domain.common.pagination.PageableModel;
import com.modulythe.framework.domain.common.pagination.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// note: Does I need to add DTO's if post request ?
// The domain is already protected btw

/**
 * Utility class for Controllers to simplify the creation of domain objects
 * related to pagination and filtering.
 */
public class ControllerUtils {

    private ControllerUtils() {
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
}
