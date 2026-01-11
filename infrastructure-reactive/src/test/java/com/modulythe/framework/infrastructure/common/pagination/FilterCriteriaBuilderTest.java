package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.FilterDate;
import com.modulythe.framework.domain.common.pagination.FilterString;
import com.modulythe.framework.infrastructure.exception.MalFormedQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.relational.core.query.Criteria;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilterCriteriaBuilderTest {

    private FilterCriteriaBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new FilterCriteriaBuilder();
    }

    @Test
    void build_ShouldReturnEmptyCriteria_WhenListIsNull() {
        Criteria result = builder.build(null);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void build_ShouldReturnEmptyCriteria_WhenListIsEmpty() {
        Criteria result = builder.build(Collections.emptyList());
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void build_ShouldBuildStandardLikeSearch_WhenNoSpecialConfiguration() {
        // Given
        FilterString filter = new FilterString("name", "John");

        // When
        Criteria result = builder.build(List.of(filter));

        // Then
        assertThat(result.toString()).contains("name LIKE '%John%'");
    }

    @Test
    void build_ShouldBuildExactMatch_WhenConfigured() {
        // Given
        builder.withExactMatchFields(List.of("status"));
        FilterString filter = new FilterString("status", "ACTIVE");

        // When
        Criteria result = builder.build(List.of(filter));

        // Then
        // R2DBC Criteria toString representation might vary, but essentially checks equality
        assertThat(result.toString()).contains("status = 'ACTIVE'");
    }

    @Test
    void build_ShouldBuildDateRange_WhenFieldIsDateAndFormatIsValid() {
        // Given
        builder.withDateFields(List.of("createdDate"));
        String dateStr = "2023-10-05";
        FilterString filter = new FilterString("createdDate", dateStr);

        // When
        Criteria result = builder.build(List.of(filter));

        // Then
        // Should produce >= StartOfDay AND < EndOfDay
        // Verification of specific timestamps is hard via toString, but we check presence of operators
        assertThat(result.toString())
                .contains("createdDate >= '2023-10-05")
                .contains("createdDate < '2023-10-06");
    }

    @Test
    void build_ShouldThrowMalFormedQueryException_WhenFieldIsDateAndFormatIsInvalid() {
        // Given
        builder.withDateFields(List.of("createdDate"));
        FilterString filter = new FilterString("createdDate", "05/10/2023"); // Invalid format

        // When/Then
        assertThatThrownBy(() -> builder.build(List.of(filter)))
                .isInstanceOf(MalFormedQueryException.class)
                .hasMessageContaining("Invalid date format")
                .hasMessageContaining("Expected format: YYYY-MM-DD");
    }

    @Test
    void build_ShouldBuildMultiFieldSearch_WhenConfigured() {
        // Given
        builder.withMultiFieldSearch("search", List.of("firstName", "lastName"));
        FilterString filter = new FilterString("search", "doe");

        // When
        Criteria result = builder.build(List.of(filter));

        // Then
        // Should be (firstName LIKE %doe% OR lastName LIKE %doe%)
        assertThat(result.toString())
                .contains("firstName LIKE '%doe%'")
                .contains("OR")
                .contains("lastName LIKE '%doe%'");
    }

    @Test
    void build_ShouldHandleNativeFilterDate_Between() {
        // Given
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        FilterDate filter = FilterDate.builder()
                .startDate(start)
                .endDate(end)
                .filterDateType(FilterDate.FilterDateType.BETWEEN)
                .name("publishDate")
                .build();

        // When
        Criteria result = builder.build(List.of(filter));

        // Then
        assertThat(result.toString()).contains("publishDate BETWEEN");
    }
}
