package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.*;
import org.springframework.data.relational.core.query.Criteria;

import java.util.List;

/**
 * Utility class to build Spring Data Relational (R2DBC) Criteria from Domain Filters.
 * <p>
 * This class translates a list of domain {@link Filter} objects into a {@link Criteria} chain
 * suitable for use with ReactiveCrudRepository or R2dbcEntityTemplate.
 * </p>
 */
public class FilterCriteriaBuilder {

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
            if (c != null) {
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

        return switch (filter.getType()) {
            case STRING -> {
                FilterString fs = (FilterString) filter;
                String value = fs.getValue();
                // Let's treat it as LIKE
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
            default -> null; // Or throw exception
        };
    }
}
