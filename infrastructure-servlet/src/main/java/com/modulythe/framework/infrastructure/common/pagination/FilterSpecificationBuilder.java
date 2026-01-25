package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.*;
import com.modulythe.framework.infrastructure.common.security.SqlSanitizer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

/**
 * Utility class to build Spring Data JPA Specifications from Domain Filters.
 * <p>
 * This class translates a list of domain {@link Filter} objects into a {@link Specification}
 * suitable for use with JpaSpecificationExecutor.
 * </p>
 *
 * @param <T> the entity type
 */
public class FilterSpecificationBuilder<T> {

    /**
     * Builds a single {@link Specification} combining all the provided filters with AND logic.
     *
     * @param filters the list of domain filters.
     * @return a {@link Specification} representing the combined filters.
     */
    public Specification<T> build(List<Filter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            Predicate predicate = cb.conjunction();
            for (Filter filter : filters) {
                Predicate p = toPredicate(filter, root, cb);
                if (p != null) {
                    predicate = cb.and(predicate, p);
                }
            }
            return predicate;
        };
    }

    private Predicate toPredicate(Filter filter, Root<T> root, CriteriaBuilder cb) {
        String property = filter.getName();
        // Simple support for nested properties (dot notation) could be added here if needed
        Path<?> path = root.get(property);

        return switch (filter.getType()) {
            case STRING -> {
                FilterString fs = (FilterString) filter;
                // Defaulting to case-insensitive like with escaped wildcards for security
                if (path.getJavaType() == String.class) {
                    String sanitizedValue = SqlSanitizer.wrapWithWildcards(fs.getValue()).toLowerCase();
                    yield cb.like(cb.lower((Path<String>) path), sanitizedValue, '\\');
                }
                yield cb.equal(path, fs.getValue());
            }
            case BOOLEAN -> {
                FilterBoolean fb = (FilterBoolean) filter;
                yield cb.equal(path, fb.getValue());
            }
            case NUMBER -> {
                FilterNumber fn = (FilterNumber) filter;
                yield cb.equal(path, fn.getValue());
            }
            case DATE -> {
                FilterDate fd = (FilterDate) filter;
                Path<LocalDate> datePath = (Path<LocalDate>) path;
                yield switch (fd.getFilterDateType()) {
                    case BETWEEN -> cb.between(datePath, fd.getStartDate(), fd.getEndDate());
                    case AFTER -> cb.greaterThan(datePath, fd.getStartDate());
                    case BEFORE -> cb.lessThan(datePath, fd.getEndDate());
                };
            }
            case RANGE -> {
                FilterRange fr = (FilterRange) filter;
                // Assuming integer range
                Path<Integer> intPath = (Path<Integer>) path;
                yield cb.between(intPath, fr.getMin(), fr.getMax());
            }
            case LIST -> {
                FilterList fl = (FilterList) filter;
                yield path.in(fl.getValues().getValues());
            }
            default -> null;
        };
    }
}
