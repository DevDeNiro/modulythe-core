package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.*;
import com.modulythe.framework.infrastructure.common.security.SqlSanitizer;
import com.modulythe.framework.infrastructure.exception.MalFormedQueryException;
import org.springframework.data.relational.core.query.Criteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Utility class to build Spring Data Relational (R2DBC) Criteria from Domain Filters.
 * <p>
 * This class translates a list of domain {@link Filter} objects into a {@link Criteria} chain
 * suitable for use with ReactiveCrudRepository or R2dbcEntityTemplate.
 * </p>
 */
public class FilterCriteriaBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final Map<String, List<String>> multiFieldMappings;
    private final Set<String> dateFields;
    private final Set<String> exactMatchFields;

    public FilterCriteriaBuilder() {
        this.multiFieldMappings = new HashMap<>();
        this.dateFields = new HashSet<>();
        this.exactMatchFields = new HashSet<>();
    }

    /**
     * Maps a single filter name to multiple entity fields.
     * Use this for search functionality (e.g. "search" -> ["title", "description"]).
     *
     * @param filterName   The name of the filter (e.g. "search")
     * @param entityFields The list of entity fields to search in (OR logic).
     * @return this builder for chaining.
     */
    public FilterCriteriaBuilder withMultiFieldSearch(String filterName, List<String> entityFields) {
        this.multiFieldMappings.put(filterName, entityFields);
        return this;
    }

    /**
     * Registers fields that should be treated as Date fields.
     * If a FilterString is encountered for these fields, it will be parsed as a Date (YYYY-MM-DD)
     * and converted to a day range (start of day to the end of day).
     *
     * @param fields The list of field names.
     * @return this builder for chaining.
     */
    public FilterCriteriaBuilder withDateFields(List<String> fields) {
        if (fields != null) {
            this.dateFields.addAll(fields);
        }
        return this;
    }

    /**
     * Registers fields that should be treated as Exact Match fields.
     * If a FilterString is encountered for these fields, it will be treated as equality check
     * instead of a LIKE search.
     *
     * @param fields The list of field names.
     * @return this builder for chaining.
     */
    public FilterCriteriaBuilder withExactMatchFields(List<String> fields) {
        if (fields != null) {
            this.exactMatchFields.addAll(fields);
        }
        return this;
    }

    /**
     * Builds a single {@link Criteria} object combining all the provided filters with AND logic.
     *
     * @param filters the list of domain filters.
     * @return a {@link Criteria} representing the combined filters, or {@link Criteria#empty()} if the list is null or empty.
     */
    public Criteria build(List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return Criteria.empty();
        }

        Criteria criteria = Criteria.empty();
        for (Filter filter : filters) {
            Criteria c = toCriteria(filter);
            if (c != null && !c.isEmpty()) {
                if (criteria.isEmpty()) {
                    criteria = c;
                } else {
                    criteria = criteria.and(c);
                }
            }
        }
        return criteria;
    }

    private Criteria toCriteria(Filter filter) {
        String property = filter.getName();

        // Handle multi-field search (OR logic)
        if (multiFieldMappings.containsKey(property)) {
            return buildMultiFieldCriteria(property, filter);
        }

        return switch (filter.getType()) {
            case STRING -> buildStringCriteria(property, (FilterString) filter);
            case BOOLEAN -> Criteria.where(property).is(((FilterBoolean) filter).getValue());
            case NUMBER -> Criteria.where(property).is(((FilterNumber) filter).getValue());
            case DATE -> buildDateCriteria(property, (FilterDate) filter);
            case RANGE -> {
                FilterRange fr = (FilterRange) filter;
                yield Criteria.where(property).between(fr.getMin(), fr.getMax());
            }
            case LIST -> {
                FilterList fl = (FilterList) filter;
                yield Criteria.where(property).in(fl.getValues().getValues());
            }
            default -> throw new UnsupportedOperationException("Unsupported filter type: " + filter.getType());
        };
    }


    /**
     * Builds criteria for multi-field search logic (OR condition across multiple fields).
     *
     * @param property The filter property name.
     * @param filter   The filter containing the search value.
     * @return A {@link Criteria} object representing the OR logic, or null if fields are empty.
     */
    private Criteria buildMultiFieldCriteria(String property, Filter filter) {
        List<String> fields = multiFieldMappings.get(property);
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        if (filter instanceof FilterString fs) {
            String sanitizedValue = SqlSanitizer.wrapWithWildcards(fs.getValue());
            Criteria orCriteria = null;
            for (String field : fields) {
                Criteria c = Criteria.where(field).like(sanitizedValue).ignoreCase(true);
                if (orCriteria == null) {
                    orCriteria = c;
                } else {
                    orCriteria = orCriteria.or(c);
                }
            }
            return orCriteria;
        }
        return null;
    }

    /**
     * Builds criteria for String-typed filters.
     * This handles standard LIKE searches, exact matches (if configured), and date parsing from strings (if configured).
     *
     * @param property The property name.
     * @param filter   The string filter.
     * @return The resulting {@link Criteria}.
     */
    private Criteria buildStringCriteria(String property, FilterString filter) {
        String value = filter.getValue();

        // 1. Check if it's a date field
        if (dateFields.contains(property)) {
            return parseDateCriteria(property, value);
        }

        // 2. Check if it's an exact match field
        if (exactMatchFields.contains(property)) {
            return Criteria.where(property).is(value);
        }

        // 3. Default to LIKE with sanitized wildcards for security
        String sanitizedValue = SqlSanitizer.wrapWithWildcards(value);
        return Criteria.where(property).like(sanitizedValue).ignoreCase(true);
    }

    /**
     * Parses a string value into a Date range criteria (Start of Day to End of Day).
     *
     * @param property The property name.
     * @param value    The date string (YYYY-MM-DD).
     * @return A {@link Criteria} for the date range.
     * @throws MalFormedQueryException if the date string format is invalid.
     */
    private Criteria parseDateCriteria(String property, String value) {
        try {
            LocalDate date = LocalDate.parse(value, DATE_FORMATTER);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            // Assume standard Spring Data convention for ranges (inclusive, exclusive)
            return Criteria.where(property).greaterThanOrEquals(startOfDay).and(property).lessThan(endOfDay);
        } catch (DateTimeParseException e) {
            throw new MalFormedQueryException("Invalid date format for filter '" + property + "'. Expected format: YYYY-MM-DD. Value: " + value);
        }
    }

    /**
     * Builds criteria for native {@link FilterDate} objects.
     *
     * @param property The property name.
     * @param fd       The date filter object.
     * @return The resulting {@link Criteria}.
     */
    private Criteria buildDateCriteria(String property, FilterDate fd) {
        return switch (fd.getFilterDateType()) {
            case BETWEEN -> Criteria.where(property).between(fd.getStartDate(), fd.getEndDate());
            case AFTER -> Criteria.where(property).greaterThan(fd.getStartDate());
            case BEFORE -> Criteria.where(property).lessThan(fd.getEndDate());
        };
    }
}
