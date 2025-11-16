package com.modulythe.framework.domain.model;

//import com.core.ddd.library.domain.model.BaseDomainEvent;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class BaseDomainEventTest {
//
//    @Test
//    void testBuilder() {
//        UUID id = UUID.randomUUID();
//        LocalDateTime occurredOn = LocalDateTime.now();
//
//        TestDomainEvent event = new TestDomainEvent.Builder()
//                .withId(id)
//                .withOccurredOn(occurredOn)
//                .typeCreation(BaseDomainEvent.DomainEventTypes.CREATED)
//                .build();
//
//        assertEquals(id, event.getId());
//        assertEquals(occurredOn, event.getOccurredOn());
//        assertEquals(BaseDomainEvent.DomainEventTypes.CREATED, event.getEventType());
//    }
//
//    // Concrete implementation for testing
////    static class TestDomainEvent extends BaseDomainEvent<TestDomainEvent> {
////        static class Builder extends BaseDomainEvent.Builder<TestDomainEvent, Builder> {
////            @Override
////            protected Class<TestDomainEvent> getType() {
////                return TestDomainEvent.class;
////            }
////
////            @Override
////            protected TestDomainEvent createDomainEvent() {
////                return new TestDomainEvent();
////            }
////
////            @Override
////            protected Builder getThis() {
////                return this;
////            }
////        }
////    }
//}