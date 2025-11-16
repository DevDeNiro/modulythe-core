package com.modulythe.framework.domain.model;


import com.modulythe.framework.domain.ddd.BaseRootAggregate;
import com.modulythe.framework.domain.event.BaseDomainEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class BaseRootAggregateTest {

    @Test
    @Disabled
    void testAddDomainEvent() {
        // Create a concrete implementation
        TestRootAggregate aggregate = new TestRootAggregate(UniqueId.generate());
//        TestDomainEvent event = new TestDomainEvent();

//        aggregate.addDomainEvent(event);

        assertEquals(1, aggregate.getDomainEvents().size());
//        assertEquals(event, aggregate.getDomainEvents().get(0));
    }

    @Test
    void testClearEvents() {
        TestRootAggregate aggregate = new TestRootAggregate(UniqueId.generate());
//        aggregate.addDomainEvent(new TestDomainEvent());

        aggregate.clearEvents();

        assertEquals(0, aggregate.getDomainEvents().size());
    }

    static class TestRootAggregate extends BaseRootAggregate<TestRootAggregate, UniqueId> {
        public TestRootAggregate(UniqueId id) {
            super(TestRootAggregate.class, new UniqueId(id));
        }

        public List<BaseDomainEvent<?>> getDomainEvents() {
            return this.domainEvents;
        }
    }

//    static class TestDomainEvent extends BaseDomainEvent<TestDomainEvent> {
}


