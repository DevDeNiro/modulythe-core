package com.modulythe.framework.presentation.pagination;

public class FilterNumberDto extends FilterDto {
    private Double value; // Using Double to accommodate both integers and decimals

    public FilterNumberDto() {
        super();
        setType("NUMBER"); // Ensure the type is set for Jackson deserialization
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
