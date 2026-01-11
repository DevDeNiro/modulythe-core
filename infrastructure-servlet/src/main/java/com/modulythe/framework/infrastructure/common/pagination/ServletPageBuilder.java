package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.PageModel;
import com.modulythe.framework.domain.common.pagination.PageableModel;

import java.util.List;

/**
 * Utility class to build a Domain {@link PageModel} from blocking sources.
 * <p>
 * This class facilitates the construction of a paginated response in a blocking environment (e.g., using JPA/Tomcat).
 * It combines a {@link List} of content and a total element count into a {@link PageModel}.
 * </p>
 */
public final class ServletPageBuilder {

    private ServletPageBuilder() {
        // Utility class
    }

    /**
     * Builds a {@link PageModel} by combining the content list and total count.
     *
     * @param <T>           the type of the content elements.
     * @param content       the list of content elements for the current page.
     * @param totalElements the total number of elements across all pages.
     * @param pageable      the original {@link PageableModel} request containing page number and size.
     * @return the constructed {@link PageModel}.
     */
    public static <T> PageModel<T> buildPage(
            List<T> content,
            long totalElements,
            PageableModel pageable) {

        return PageModel.builder(content)
                .itemsPerPage(pageable.getItemsPerPage())
                .pageNumber(pageable.getPageNumber())
                .totalItems(totalElements)
                .build();
    }
}
