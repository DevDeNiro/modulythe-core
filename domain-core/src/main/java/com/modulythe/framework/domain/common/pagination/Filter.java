package com.modulythe.framework.domain.common.pagination;

import com.modulythe.framework.domain.validation.Validate;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract base class for all filter types used in pagination.
 * <p>
 * This class defines the common properties of a filter, such as its name and type.
 * It also implements the {@link Validate} interface to ensure the integrity of the filter data.
 * </p>
 */
public abstract class Filter implements Validate<Filter> {

    @NotNull
    private final String name;
    @NotNull
    private final FilterType type;

    protected Filter(FilterBuilder<?, ?> builderFilter, FilterType type) {
        this.name = Objects.requireNonNull(builderFilter.getName(), "Filter name cannot be null");
        this.type = Objects.requireNonNull(type, "Filter type cannot be null");
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

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
