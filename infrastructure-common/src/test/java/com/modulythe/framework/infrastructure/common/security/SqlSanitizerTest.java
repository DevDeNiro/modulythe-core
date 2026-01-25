package com.modulythe.framework.infrastructure.common.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class SqlSanitizerTest {

    @Test
    void shouldEscapePercentWildcard() {
        String result = SqlSanitizer.escapeWildcards("100% complete");
        assertEquals("100\\% complete", result);
    }

    @Test
    void shouldEscapeUnderscoreWildcard() {
        String result = SqlSanitizer.escapeWildcards("test_value");
        assertEquals("test\\_value", result);
    }

    @Test
    void shouldEscapeBackslash() {
        String result = SqlSanitizer.escapeWildcards("path\\to\\file");
        assertEquals("path\\\\to\\\\file", result);
    }

    @Test
    void shouldEscapeMultipleWildcards() {
        String result = SqlSanitizer.escapeWildcards("50%_discount");
        assertEquals("50\\%\\_discount", result);
    }

    @Test
    void shouldHandleNullValue() {
        String result = SqlSanitizer.escapeWildcards(null);
        assertEquals("", result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldHandleNullAndEmptyValues(String input) {
        String result = SqlSanitizer.wrapWithWildcards(input);
        assertEquals("%%", result);
    }

    @Test
    void shouldWrapWithWildcards() {
        String result = SqlSanitizer.wrapWithWildcards("search");
        assertEquals("%search%", result);
    }

    @Test
    void shouldWrapWithWildcardsAndEscape() {
        String result = SqlSanitizer.wrapWithWildcards("100%");
        assertEquals("%100\\%%", result);
    }

    @Test
    void shouldWrapWithPrefixWildcard() {
        String result = SqlSanitizer.wrapWithPrefixWildcard("suffix");
        assertEquals("%suffix", result);
    }

    @Test
    void shouldWrapWithSuffixWildcard() {
        String result = SqlSanitizer.wrapWithSuffixWildcard("prefix");
        assertEquals("prefix%", result);
    }

    @ParameterizedTest
    @CsvSource({
            "'%%%%',  '%\\%\\%\\%\\%%'",
            "'test',  '%test%'",
            "'a_b',   '%a\\_b%'",
            "'',      '%%'"
    })
    void shouldHandleVariousInputs(String input, String expected) {
        String result = SqlSanitizer.wrapWithWildcards(input);
        assertEquals(expected, result);
    }

    @Test
    void shouldPreventWildcardInjectionAttack() {
        // Attacker tries to inject wildcards to slow down queries
        String maliciousInput = "%%%%%%%%%%%%%%%%";
        String result = SqlSanitizer.escapeWildcards(maliciousInput);
        
        // All % should be escaped
        assertFalse(result.contains("%%"));
        assertTrue(result.contains("\\%"));
    }
}
