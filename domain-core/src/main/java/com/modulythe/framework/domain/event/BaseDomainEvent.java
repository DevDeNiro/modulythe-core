package com.modulythe.framework.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all types of domain events.
 */
public abstract class BaseDomainEvent<T extends BaseDomainEvent<T>> implements Serializable {

    protected UUID eventId;
    protected LocalDateTime occurredOn;
    protected DomainEventTypes eventType;
    protected Class<T> type;

    protected BaseDomainEvent(Class<T> type, DomainEventTypes eventType) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.eventType = eventType != null ? eventType : DomainEventTypes.CREATED;
        this.type = type;
    }

    public UUID getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public DomainEventTypes getEventType() {
        return eventType;
    }

    public Class<T> getType() {
        return type;
    }

    public enum DomainEventTypes {
        CREATED,        // Newly created object
        UPDATED,        // Partial or full update
        DELETED,        // Soft or hard deletion

        ACTIVATED,      // Marked as active (e.g. reactivating an account)
        DEACTIVATED,    // Marked as inactive (e.g. disabling a user)
        ARCHIVED,       // Archived (moved out of main workflow)
        RESTORED,       // Restored from archive or soft delete

        PUBLISHED,      // Made public (e.g. article, offer)
        UNPUBLISHED,    // Taken offline

        CANCELLED,      // Operation cancelled (e.g. order)
        COMPLETED,      // Task or workflow completed
        FAILED,         // Processing failure (e.g. payment)

        EXPIRED,        // Expiration reached (e.g. token, coupon)
        LOCKED,         // Locked
        UNLOCKED        // Unlocked
    }

    protected abstract static class Builder<T extends BaseDomainEvent<T>, B extends Builder<T, B>> {
        protected T domainEvent;
        protected B builder;

        protected abstract Class<T> getType();

        protected abstract T createDomainEvent();

        protected abstract B getThis();

        protected Builder() {
            domainEvent = createDomainEvent();
            builder = getThis();
        }

        public B withId(UUID id) {
            domainEvent.eventId = id;
            return builder;
        }

        public B withOccurredOn(LocalDateTime occurredOn) {
            domainEvent.occurredOn = occurredOn;
            return builder;
        }

        public B typeCreation() {
            domainEvent.eventType = DomainEventTypes.CREATED;
            return builder;
        }

        public B typeUpdate() {
            domainEvent.eventType = DomainEventTypes.UPDATED;
            return builder;
        }

        public B typeDeletion() {
            domainEvent.eventType = DomainEventTypes.DELETED;
            return builder;
        }

        public T build() {
            return domainEvent;
        }
    }
}