package com.modulythe.framework.domain.ddd;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BaseValueObjectTest {

    static class TestValueObject extends BaseValueObject<TestValueObject> {
        private final String value1;
        private final int value2;

        protected TestValueObject(String value1, int value2) {
            super(TestValueObject.class);
            this.value1 = value1;
            this.value2 = value2;
        }

        @Override
        protected List<Object> attributesToIncludeInEqualityCheck() {
            return Arrays.asList(value1, value2);
        }
    }

    @Test
    void testEquality() {
        TestValueObject vo1 = new TestValueObject("test", 1);
        TestValueObject vo2 = new TestValueObject("test", 1);
        TestValueObject vo3 = new TestValueObject("other", 1);

        assertEquals(vo1, vo2);
        assertNotEquals(vo1, vo3);
        assertEquals(vo1.hashCode(), vo2.hashCode());
        assertNotEquals(vo1.hashCode(), vo3.hashCode());
    }

    @Test
    void testToString() {
        TestValueObject vo = new TestValueObject("test", 1);
        String string = vo.toString();
        assertTrue(string.contains("TestValueObject"));
        assertTrue(string.contains("test"));
        assertTrue(string.contains("1"));
    }
}
