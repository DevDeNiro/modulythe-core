package com.modulythe.framework.domain.ddd;

import com.modulythe.framework.domain.event.BaseDomainEvent;
import com.modulythe.framework.domain.model.UniqueId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseRootAggregateTest {

    static class TestEvent extends BaseDomainEvent<TestEvent> {
        protected TestEvent() {
            super(TestEvent.class, DomainEventTypes.CREATED);
        }
    }

    static class TestAggregate extends BaseRootAggregate<TestAggregate, UniqueId> {
        protected TestAggregate(UniqueId id) {
            super(TestAggregate.class, id);
        }

        public void doSomething() {
            this.addDomainEvent(new TestEvent());
            this.recordModification();
        }
    }

    @Test
    void testDomainEvents() {
        TestAggregate aggregate = new TestAggregate(UniqueId.generate());
        assertTrue(aggregate.getDomainEvents().isEmpty());

        aggregate.doSomething();
        assertEquals(1, aggregate.getDomainEvents().size());
        assertInstanceOf(TestEvent.class, aggregate.getDomainEvents().get(0));

        aggregate.clearEvents();
        assertTrue(aggregate.getDomainEvents().isEmpty());
    }

    @Test
    void testVersioning() {
        TestAggregate aggregate = new TestAggregate(UniqueId.generate());
        assertEquals(-1, aggregate.getVersion());
    }

    @Test
    void testToString() {
        TestAggregate aggregate = new TestAggregate(UniqueId.generate());
        assertNotNull(aggregate.toString());
        assertTrue(aggregate.toString().contains("BaseAggregateRoot"));
    }
}
