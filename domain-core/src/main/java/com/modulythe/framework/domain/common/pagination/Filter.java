package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.validation.Validate;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Objects;

public abstract class Filter implements Validate<Filter> {

    @NotNull
    private final String name;
    @NotNull
    private final FilterType type;

    protected Filter(FilterBuilder<?, ?> builderFilter, FilterType type) {
        this.name = Objects.requireNonNull(builderFilter.getName(), "Filter name cannot be null");
        this.type = Objects.requireNonNull(type, "Filter type cannot be null");
        validate(this);
    }

    public String getName() {
        return name;
    }

    public FilterType getType() {
        return type;
    }

    public enum FilterType {
        STRING,
        NUMBER,
        DATE,
        RANGE,
        BOOLEAN,
        LIST
    }

    public static FilterType fromName(String name) {
        return Arrays.stream(FilterType.values())
                .filter(filterType -> filterType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown filter type: " + name));
    }
}
