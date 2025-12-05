package com.modulythe.framework.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BusinessErrorTest {

    @Test
    void testCreation() {
        BusinessError error = new BusinessError("ERR_001", "Error occurred");
        assertEquals("ERR_001", error.getCode());
        assertEquals("Error occurred", error.getMessage());
        assertTrue(error.getAdditionalInfo().isEmpty());
    }

    @Test
    void testCreationWithInfo() {
        BusinessError error = new BusinessError("ERR_001", "Error", Map.of("key", "value"));
        assertEquals("value", error.getAdditionalInfo().get("key"));
    }

    @Test
    void testNullChecks() {
        assertThrows(IllegalArgumentException.class, () -> new BusinessError(null, "msg"));
        assertThrows(IllegalArgumentException.class, () -> new BusinessError("code", null));
    }

    @Test
    void testEquality() {
        BusinessError e1 = new BusinessError("CODE", "Msg");
        BusinessError e2 = new BusinessError("CODE", "Msg");
        assertEquals(e1, e2);
    }

    @Test
    void testToString() {
        BusinessError error = new BusinessError("CODE", "Msg");
        assertTrue(error.toString().contains("CODE"));
    }
}
