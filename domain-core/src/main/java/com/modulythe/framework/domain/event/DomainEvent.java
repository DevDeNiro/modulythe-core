package com.modulythe.framework.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent extends Serializable {
    UUID getId();

    LocalDateTime getOccurredOn();

    DomainEventType getEventType();

    Class<? extends DomainEvent> getType();

    enum DomainEventType {
        CREATED, UPDATED, DELETED
    }
}