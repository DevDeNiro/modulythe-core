package com.modulythe.framework.infrastructure.common.security;

/**
 * Utility class for sanitizing SQL-related input to prevent injection attacks
 * and wildcard abuse in LIKE queries.
 */
public final class SqlSanitizer {

    private SqlSanitizer() {
        // Utility class
    }

    /**
     * Escapes SQL LIKE wildcard characters (% and _) to prevent wildcard injection attacks.
     * <p>
     * This prevents attackers from injecting patterns like "%%%%%" that could cause
     * performance degradation or return unintended results.
     * </p>
     *
     * @param value The input string to sanitize.
     * @return The sanitized string with escaped wildcards, or empty string if input is null.
     */
    public static String escapeWildcards(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")  // Escape backslash first
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Wraps a value with LIKE wildcards for partial matching.
     * The value is first sanitized to prevent wildcard injection.
     *
     * @param value The input string.
     * @return The sanitized value wrapped with % for LIKE queries.
     */
    public static String wrapWithWildcards(String value) {
        return "%" + escapeWildcards(value) + "%";
    }

    /**
     * Wraps a value with a prefix wildcard for suffix matching.
     * The value is first sanitized to prevent wildcard injection.
     *
     * @param value The input string.
     * @return The sanitized value with prefix % for LIKE queries.
     */
    public static String wrapWithPrefixWildcard(String value) {
        return "%" + escapeWildcards(value);
    }

    /**
     * Wraps a value with a suffix wildcard for prefix matching.
     * The value is first sanitized to prevent wildcard injection.
     *
     * @param value The input string.
     * @return The sanitized value with suffix % for LIKE queries.
     */
    public static String wrapWithSuffixWildcard(String value) {
        return escapeWildcards(value) + "%";
    }
}
