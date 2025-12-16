package com.modulythe.framework.domain.common.pagination;

/**
 * Abstract builder class for creating {@link Filter} instances.
 * <p>
 * This builder provides a fluent API for setting the common properties of a filter,
 * such as its name. Concrete implementations should extend this class to add
 * type-specific properties.
 * </p>
 *
 * @param <T> the type of the filter being built
 * @param <B> the type of the builder subclass
 */
public abstract class FilterBuilder<T extends Filter, B extends FilterBuilder<T, B>> {
    private String name;

    public B name(String name) {
        this.name = name;
        return self();
    }

    protected String getName() {
        return name;
    }

    protected abstract B self();

    public T build() {
        return this.buildFilter();
    }

    protected abstract T buildFilter();
}
