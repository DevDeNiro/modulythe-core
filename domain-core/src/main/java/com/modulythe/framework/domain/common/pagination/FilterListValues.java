package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.ddd.BaseValueObject;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@SuppressWarnings("java:S2160") // "false positive"
public final class FilterListValues extends BaseValueObject<FilterListValues> {
    @NotNull
    private final List<String> values;

    public FilterListValues(List<String> values) {
        super(FilterListValues.class);
        this.values = List.copyOf(values);
        validate(this);
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return List.of(values);
    }
}
