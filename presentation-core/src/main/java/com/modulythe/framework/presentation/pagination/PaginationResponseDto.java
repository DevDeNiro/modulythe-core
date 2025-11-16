package com.modulythe.framework.presentation.pagination;

import java.util.List;

// Encapsulate the JSON response with Pagination details
public record PaginationResponseDto<T>(
        List<T> results,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
//        String query
) {
}
