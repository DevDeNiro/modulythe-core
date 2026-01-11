package com.modulythe.framework.infrastructure.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

class AbstractR2dbcConverterTest {

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

    static class TestStatusConverter extends AbstractR2dbcConverter<TestStatus, String> {
        public TestStatusConverter() {
            super(TestStatus::getCode, TestStatus.values());
        }
    }

    private final TestStatusConverter converter = new TestStatusConverter();

    @Nested
    @DisplayName("WritingConverter (Entity -> DB)")
    class WritingConverterTest {

        private final Converter<TestStatus, String> writingConverter = converter.getWritingConverter();

        @Test
        @DisplayName("Should convert enum to code")
        void shouldConvertEnumToCode() {
            assertEquals("ACT", writingConverter.convert(TestStatus.ACTIVE));
            assertEquals("INA", writingConverter.convert(TestStatus.INACTIVE));
        }

        @Test
        @DisplayName("Should convert null to null")
        void shouldConvertNullToNull() {
            assertNull(writingConverter.convert(null));
        }
    }

    @Nested
    @DisplayName("ReadingConverter (DB -> Entity)")
    class ReadingConverterTest {

        private final Converter<String, TestStatus> readingConverter = converter.getReadingConverter();

        @Test
        @DisplayName("Should convert valid code to enum")
        void shouldConvertCodeToEnum() {
            assertEquals(TestStatus.ACTIVE, readingConverter.convert("ACT"));
            assertEquals(TestStatus.INACTIVE, readingConverter.convert("INA"));
        }

        @Test
        @DisplayName("Should convert null to null")
        void shouldConvertNullToNull() {
            assertNull(readingConverter.convert(null));
        }

        @Test
        @DisplayName("Should throw exception for unknown code")
        void shouldThrowExceptionForUnknownCode() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                readingConverter.convert("UNKNOWN_CODE");
            });
            assertTrue(exception.getMessage().contains("Unknown value: UNKNOWN_CODE"));
        }
    }
}
