package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.*;
import org.springframework.data.relational.core.query.Criteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to build Spring Data Relational (R2DBC) Criteria from Domain Filters.
 * <p>
 * This class translates a list of domain {@link Filter} objects into a {@link Criteria} chain
 * suitable for use with ReactiveCrudRepository or R2dbcEntityTemplate.
 * </p>
 */
public class FilterCriteriaBuilder {

    private final Map<String, List<String>> multiFieldMappings;

    public FilterCriteriaBuilder() {
        this.multiFieldMappings = new HashMap<>();
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
            List<String> fields = multiFieldMappings.get(property);
            if (fields == null || fields.isEmpty()) {
                return null;
            }
            if (filter instanceof FilterString fs) {
                String value = fs.getValue();
                Criteria orCriteria = null;
                for (String field : fields) {
                    Criteria c = Criteria.where(field).like("%" + value + "%").ignoreCase(true);
                    if (orCriteria == null) {
                        orCriteria = c;
                    } else {
                        orCriteria = orCriteria.or(c);
                    }
                }
                return orCriteria;
            }
            // NOTE: Add support for other types ?
        }

        return switch (filter.getType()) {
            case STRING -> {
                FilterString fs = (FilterString) filter;
                String value = fs.getValue();
                // Treat it as LIKE
                yield Criteria.where(property).like("%" + value + "%").ignoreCase(true);
            }
            case BOOLEAN -> {
                FilterBoolean fb = (FilterBoolean) filter;
                yield Criteria.where(property).is(fb.getValue());
            }
            case NUMBER -> {
                FilterNumber fn = (FilterNumber) filter;
                yield Criteria.where(property).is(fn.getValue());
            }
            case DATE -> {
                FilterDate fd = (FilterDate) filter;
                yield switch (fd.getFilterDateType()) {
                    case BETWEEN -> Criteria.where(property).between(fd.getStartDate(), fd.getEndDate());
                    case AFTER -> Criteria.where(property).greaterThan(fd.getStartDate());
                    case BEFORE -> Criteria.where(property).lessThan(fd.getEndDate());
                };
            }
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
}
