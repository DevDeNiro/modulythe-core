package com.modulythe.framework.domain.ddd;

import com.modulythe.framework.domain.validation.Validate;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a base class for entities in Domain-Driven Design (DDD).
 * An entity is defined by its concrete type {@code T} and an identifier of type {@code ID} (which is a ValueObject).
 * The entity is uniquely identified by its ID.
 *
 * @param <T>  The concrete type of the entity.
 * @param <ID> The type of the entity's identifier, extending {@link BaseValueObject}.
 */
public abstract class BaseEntity<T extends BaseEntity<T, ID>, ID extends BaseValueObject<ID>> implements Validate<BaseEntity<T, ID>>, Serializable {

    private final Class<T> type;

    @NotNull
    private final ID id;

    protected BaseEntity(Class<T> type, ID id) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.id = Objects.requireNonNull(id, "Id cannot be null");
    }

    /**
     * Returns the unique identifier of the entity.
     *
     * @return The ID of the entity.
     */
    public ID getId() {
        return id;
    }

    /**
     * Returns the concrete class of the entity, useful for comparisons.
     *
     * @return The class of the entity.
     */
    protected Class<T> getType() {
        return type;
    }

    /**
     * Two entities are considered equal if and only if they share the same ID.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!type.isInstance(obj)) return false;

        T other = type.cast(obj);
        return this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s]", type.getSimpleName(), id.toString());
    }
}
