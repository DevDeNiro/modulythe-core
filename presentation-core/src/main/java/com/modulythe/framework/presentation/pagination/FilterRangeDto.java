package com.modulythe.framework.presentation.pagination;

public class FilterRangeDto extends FilterDto {
    private double min;
    private double max;

    public FilterRangeDto() {
        super();
        setType("RANGE");
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
