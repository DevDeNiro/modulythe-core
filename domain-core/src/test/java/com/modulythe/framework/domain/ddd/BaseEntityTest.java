package com.modulythe.framework.domain.ddd;

import com.modulythe.framework.domain.model.UniqueId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {

    static class TestEntity extends BaseEntity<TestEntity, UniqueId> {
        protected TestEntity(UniqueId id) {
            super(TestEntity.class, id);
        }
    }

    @Test
    void testEquality() {
        UniqueId id = UniqueId.generate();
        TestEntity entity1 = new TestEntity(id);
        TestEntity entity2 = new TestEntity(id);
        TestEntity entity3 = new TestEntity(UniqueId.generate());

        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testToString() {
        UniqueId id = UniqueId.generate();
        TestEntity entity = new TestEntity(id);
        String string = entity.toString();
        assertTrue(string.contains("TestEntity"));
        assertTrue(string.contains(id.toString()));
    }

    @Test
    void testGetters() {
        UniqueId id = UniqueId.generate();
        TestEntity entity = new TestEntity(id);
        assertEquals(id, entity.getId());
    }
}
