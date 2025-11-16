package com.modulythe.framework.presentation.pagination;

public class FilterStringDto extends FilterDto {
    private String value;

    public FilterStringDto() {
        super();
        setType("STRING");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
