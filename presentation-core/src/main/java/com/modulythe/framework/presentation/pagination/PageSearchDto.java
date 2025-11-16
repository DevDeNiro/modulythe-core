package com.modulythe.framework.presentation.pagination;

import java.util.List;

public class PageSearchDto {
    private List<FilterDto> filters;
    private PaginationDto paginationDto;

    public List<FilterDto> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDto> filters) {
        this.filters = filters;
    }

    public PaginationDto getPaginationDto() {
        return paginationDto;
    }

    public void setPaginationDto(PaginationDto paginationDto) {
        this.paginationDto = paginationDto;
    }
}