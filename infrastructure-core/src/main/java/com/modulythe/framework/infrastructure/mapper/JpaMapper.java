package com.modulythe.framework.infrastructure.mapper;

public interface JpaMapper<D, E> {
    E mapToEntity(D domain);
    D mapToDomain(E entity);
}