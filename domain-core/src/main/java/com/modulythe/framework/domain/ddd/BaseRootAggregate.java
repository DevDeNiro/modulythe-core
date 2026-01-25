package com.modulythe.framework.domain.ddd;

import com.modulythe.framework.domain.event.BaseDomainEvent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for an Aggregate Root in Domain-Driven Design (DDD).
 * It extends {@link BaseEntity} and adds versioning for optimistic locking control.
 *
 * @param <T>  The concrete type of the aggregate.
 * @param <ID> The type of the aggregate's identifier (a value object).
 */
public abstract class BaseRootAggregate<T extends BaseRootAggregate<T, ID>, ID extends BaseValueObject<ID>> extends BaseEntity<T, ID> implements Serializable {

    // Version control for the aggregate
    protected long version = -1;

    // Date and time of the last modification of the aggregate
    protected LocalDateTime lastModified;

    protected BaseRootAggregate(Class<T> type, ID id) {
        super(type, id);
        this.lastModified = LocalDateTime.now();
    }

    // Maintain a list of domain events for the AR
    private transient List<BaseDomainEvent<?>> domainEvents = new ArrayList<>();

    /**
     * Adds a domain event to the aggregate's list of events.
     *
     * @param event The domain event to add.
     */
    protected void addDomainEvent(BaseDomainEvent<?> event) {
        this.domainEvents.add(event);
    }

    /**
     * Records that the aggregate has been modified by updating the last modified timestamp.
     */
    protected void recordModification() {
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Retrieves the list of domain events recorded by this aggregate.
     *
     * @return A list of domain events.
     */
    public List<BaseDomainEvent<?>> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    /**
     * Clears the list of domain events.
     * This is typically called after the events have been dispatched.
     */
    public void clearEvents() {
        this.domainEvents.clear();
    }

    /**
     * Returns the version of the aggregate for optimistic locking.
     *
     * @return the version number
     */
    public long getVersion() {
        return version;
    }

    /**
     * Returns the latest update of the aggregate.
     *
     * @return the last date-time update.
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return super.toString() + ", BaseAggregateRoot{" +
                "version=" + version +
                ", domainEvents=" + domainEvents +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        BaseRootAggregate<?, ?> that = (BaseRootAggregate<?, ?>) object;
        return version == that.version
                && java.util.Objects.equals(domainEvents, that.domainEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), version, domainEvents);
    }
}
