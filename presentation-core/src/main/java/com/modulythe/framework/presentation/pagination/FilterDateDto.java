package com.modulythe.framework.presentation.pagination;

import com.modulythe.framework.domain.common.pagination.FilterDate;

import java.time.LocalDate;

public class FilterDateDto extends FilterDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private FilterDate.FilterDateType filterDateType;

    public FilterDateDto() {
        super();
        setType("DATE"); // Ensure the type is set for Jackson deserialization
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public FilterDate.FilterDateType getFilterDateType() {
        return filterDateType;
    }

    public void setFilterDateType(FilterDate.FilterDateType filterDateType) {
        this.filterDateType = filterDateType;
    }
}
