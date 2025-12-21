package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.ddd.BaseValueObject;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Represents a request for pagination information.
 * <p>
 * This class encapsulates the page number, the number of items per page, and
 * the sorting criteria. It is typically used to request a specific page of data from a repository.
 * </p>
 */
@SuppressWarnings("java:S2160") // "false positive"
public final class PageableModel extends BaseValueObject<PageableModel> {

    private final int pageNumber;
    private final int itemsPerPage;
    @NotNull
    private final SortModel sort;

    private PageableModel(int pageNumber, int itemsPerPage, SortModel sort) {
        super(PageableModel.class);
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
        this.sort = sort == null ? SortModel.empty() : sort;
        validate(this);
    }

    public PageableModel(int pageNumber, int itemsPerPage) {
        this(pageNumber, itemsPerPage, SortModel.empty());
    }

    public static PageableModel of(int pageNumber, int itemsPerPage, SortModel sort) {
        return new PageableModel(pageNumber, itemsPerPage, sort);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public SortModel getSort() {
        return sort;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return List.of(pageNumber, itemsPerPage, sort);
    }

    @Override
    public String toString() {
        return "PageableModel{" +
                "pageNumber=" + pageNumber +
                ", itemsPerPage=" + itemsPerPage +
                ", sort=" + sort +
                '}';
    }
}
