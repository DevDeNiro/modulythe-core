package com.modulythe.framework.presentation.pagination;

public class FilterBooleanDto extends FilterDto {
    private Boolean value;

    public FilterBooleanDto() {
        super();
        setType("BOOLEAN");
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
