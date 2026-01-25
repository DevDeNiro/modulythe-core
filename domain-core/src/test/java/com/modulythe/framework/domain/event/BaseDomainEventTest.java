package com.modulythe.framework.domain.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseDomainEventTest {

    static class TestEvent extends BaseDomainEvent<TestEvent> {
        public TestEvent() {
            super(TestEvent.class, DomainEventTypes.CREATED);
        }
    }

    @Test
    void testCreation() {
        TestEvent event = new TestEvent();
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
        assertEquals(BaseDomainEvent.DomainEventTypes.CREATED, event.getEventType());
        assertEquals(TestEvent.class, event.getType());
    }
}
