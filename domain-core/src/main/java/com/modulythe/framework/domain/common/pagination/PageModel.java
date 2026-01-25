package com.modulythe.framework.domain.common.pagination;


import com.modulythe.framework.domain.validation.Validate;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a page of data.
 * <p>
 * This class contains the content of a page, as well as metadata about the pagination,
 * such as the current page number, items per page, and total items.
 * </p>
 *
 * @param <T> the type of the items in the page
 */
public final class PageModel<T> implements Validate<PageModel<T>> {

    public static <T> PageBuilder<T> builder(List<T> content) {
        return new PageBuilder<>(content);
    }

    public static class PageBuilder<T> {
        private final List<T> content;
        private int itemsPerPage;
        private int pageNumber;
        private long totalItems;

        private PageBuilder(List<T> content) {
            this.content = content;
        }

        public PageBuilder<T> itemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
            return this;
        }

        public PageBuilder<T> pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public PageBuilder<T> totalItems(long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public PageModel<T> build() {
            return new PageModel<>(this);
        }
    }

    private final int itemsPerPage;
    private final int pageNumber;

    @NotNull
    private final List<T> items;
    private final long totalItems;

    private PageModel(PageBuilder<T> builder) {
        this.itemsPerPage = builder.itemsPerPage;
        this.pageNumber = builder.pageNumber;
        this.items = builder.content;
        this.totalItems = builder.totalItems;
        validate(this);

        assertBoundaries();
        assertContent();
    }

    public void assertBoundaries() {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("PageModel number must be greater than or equal to 0");
        }

        if (itemsPerPage < 1) {
            throw new IllegalArgumentException("Items per page must be greater than 0");
        }

        if (totalItems < 0) {
            throw new IllegalArgumentException("Total items must be greater than or equal to 0");
        }
    }

    public void assertContent() {
        if ((long) pageNumber * itemsPerPage > totalItems) {
            throw new IllegalArgumentException("PageModel number is out of bounds");
        }
    }

    public boolean isFirst() {
        return pageNumber == 0;
    }

    public boolean isLast() {
        return getNumberOfPages() == pageNumber + 1;
    }

    public int getNumberOfPages() {
        if (itemsPerPage == 0) {
            return 1;
        }

        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PageModel<?> pageModel = (PageModel<?>) o;
        return itemsPerPage == pageModel.itemsPerPage
                && pageNumber == pageModel.pageNumber
                && totalItems == pageModel.totalItems
                && Objects.equals(items, pageModel.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemsPerPage, pageNumber, items, totalItems);
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "itemsPerPage=" + itemsPerPage +
                ", pageNumber=" + pageNumber +
                ", items=" + items +
                ", totalItems=" + totalItems +
                '}';
    }
}