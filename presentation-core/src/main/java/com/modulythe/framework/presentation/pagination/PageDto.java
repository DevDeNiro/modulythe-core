package com.modulythe.framework.presentation.pagination;

import java.util.List;

public class PageDto<S> {
    private final List<S> content;

    private int currentPage;
    private final long totalElements;
    private int numberOfElementsPerPage;

    private int numberOfPages;

    private boolean first;
    private boolean last;

    public PageDto(List<S> content, int currentPage, long totalElements, int numberOfElementsPerPage) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.numberOfElementsPerPage = numberOfElementsPerPage;

        this.numberOfPages = (int) Math.ceil((double) totalElements / numberOfElementsPerPage);

        this.first = currentPage == 0;
        this.last = currentPage == numberOfPages - 1;
    }
}