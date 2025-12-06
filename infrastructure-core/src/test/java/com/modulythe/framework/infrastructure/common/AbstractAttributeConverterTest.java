package com.modulythe.framework.infrastructure.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAttributeConverterTest {

    enum TestStatus {
        ACTIVE("ACT"),
        INACTIVE("INA");

        private final String code;

        TestStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    static class TestStatusAttributeConverter extends AbstractAttributeConverter<TestStatus, String> {
        public TestStatusAttributeConverter() {
            super(TestStatus::getCode, TestStatus.values());
        }
    }

    private final TestStatusAttributeConverter converter = new TestStatusAttributeConverter();

    @Test
    @DisplayName("convertToDatabaseColumn: Should convert enum to code")
    void convertToDatabaseColumn_ShouldConvertEnumToCode() {
        assertEquals("ACT", converter.convertToDatabaseColumn(TestStatus.ACTIVE));
        assertEquals("INA", converter.convertToDatabaseColumn(TestStatus.INACTIVE));
    }

    @Test
    @DisplayName("convertToDatabaseColumn: Should convert null to null")
    void convertToDatabaseColumn_ShouldConvertNullToNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    @DisplayName("convertToEntityAttribute: Should convert valid code to enum")
    void convertToEntityAttribute_ShouldConvertCodeToEnum() {
        assertEquals(TestStatus.ACTIVE, converter.convertToEntityAttribute("ACT"));
        assertEquals(TestStatus.INACTIVE, converter.convertToEntityAttribute("INA"));
    }

    @Test
    @DisplayName("convertToEntityAttribute: Should convert null to null")
    void convertToEntityAttribute_ShouldConvertNullToNull() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    @DisplayName("convertToEntityAttribute: Should throw exception for unknown code")
    void convertToEntityAttribute_ShouldThrowExceptionForUnknownCode() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            converter.convertToEntityAttribute("UNKNOWN_CODE");
        });
        assertTrue(exception.getMessage().contains("Unknown value: UNKNOWN_CODE"));
    }
}
