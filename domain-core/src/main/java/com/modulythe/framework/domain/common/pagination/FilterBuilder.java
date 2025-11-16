package com.modulythe.framework.domain.common.pagination;

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
