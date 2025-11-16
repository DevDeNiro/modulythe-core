package com.modulythe.framework.presentation.pagination;

import java.util.List;

public class FilterListDto extends FilterDto {
    private List<String> values;

    public FilterListDto() {
        super();
        setType("LIST"); // Ensure the type is set for Jackson deserialization
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
