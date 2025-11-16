package com.modulythe.framework.domain.model;

import com.modulythe.framework.domain.ddd.BaseEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class BaseEntityTest {

    @Test
    void testEquality() {
        TestEntity entity1 = new TestEntity(UniqueId.generate());
        TestEntity entity2 = new TestEntity(UniqueId.generate());

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testToString() {
        TestEntity entity = new TestEntity(UniqueId.of("TestId"));

        assertTrue(entity.toString().contains("type=TestEntity"));
        assertTrue(entity.toString().contains("id=TestId"));
    }

    // Concrete implementations for testing
    static class TestEntity extends BaseEntity<TestEntity, UniqueId> {
        public TestEntity(UniqueId id) {
            super(TestEntity.class, id);
        }
    }
}