package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.PageableModel;
import com.modulythe.framework.domain.common.pagination.SortModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Utility class responsible for mapping Domain pagination models to Spring Data infrastructure models.
 * <p>
 * This class provides static methods to convert {@link PageableModel} and {@link SortModel}
 * from the domain layer into {@link Pageable} and {@link Sort} objects used by Spring Data repositories.
 * </p>
 */
public final class PageableMapper {

    private PageableMapper() {
        // Utility class
    }

    /**
     * Converts a domain {@link PageableModel} to a Spring Data {@link Pageable}.
     *
     * @param pageableModel the domain pagination model.
     * @return a {@link Pageable} object configured with the page number, size, and sort options.
     * Returns {@link Pageable#unpaged()} if the input model is null.
     */
    public static Pageable toSpringPageable(PageableModel pageableModel) {
        if (pageableModel == null) {
            return Pageable.unpaged();
        }

        int page = pageableModel.getPageNumber();
        int size = pageableModel.getItemsPerPage();
        Sort sort = toSpringSort(pageableModel.getSort());

        return PageRequest.of(page, size, sort);
    }

    /**
     * Converts a domain {@link SortModel} to a Spring Data {@link Sort}.
     *
     * @param sortModel the domain sort model.
     * @return a {@link Sort} object containing the mapped orders.
     * Returns {@link Sort#unsorted()} if the input model is null or empty.
     */
    public static Sort toSpringSort(SortModel sortModel) {
        if (sortModel == null || sortModel.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = sortModel.getOrders().stream()
                .map(PageableMapper::toSpringOrder)
                .toList();

        return Sort.by(orders);
    }

    private static Sort.Order toSpringOrder(SortModel.Order order) {
        Sort.Direction direction = order.getDirection() == SortModel.Direction.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return new Sort.Order(direction, order.getProperty());
    }
}
