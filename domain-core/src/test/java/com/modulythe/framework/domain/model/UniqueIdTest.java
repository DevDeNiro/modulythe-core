package com.modulythe.framework.domain.model;

import com.modulythe.framework.domain.exception.BusinessException;
import com.modulythe.framework.domain.exception.InvalidUniqueIdFormatException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UniqueIdTest {

    @Test
    void testGenerate() {
        UniqueId id = UniqueId.generate();
        assertNotNull(id);
        assertNotNull(id.getValue());
    }

    @Test
    void testOfValid() {
        String uuid = UUID.randomUUID().toString();
        UniqueId id = UniqueId.of(uuid);
        assertEquals(uuid, id.getValue());
    }

    @Test
    void testEquality() {
        String uuid = UUID.randomUUID().toString();
        UniqueId id1 = UniqueId.of(uuid);
        UniqueId id2 = UniqueId.of(uuid);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testInvalidFormat() {
        assertThrows(InvalidUniqueIdFormatException.class, () -> UniqueId.of("invalid-uuid"));
    }

    @Test
    void testNullOrEmpty() {
        assertThrows(BusinessException.class, () -> UniqueId.of(null));
        assertThrows(BusinessException.class, () -> UniqueId.of(""));
        assertThrows(BusinessException.class, () -> UniqueId.of("   "));
    }
}
