package com.modulythe.framework.presentation.pagination;

import com.modulythe.framework.domain.common.pagination.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaginationMapper {

    public <T> PageDto<T> toPageDto(PageModel<T> pageModel) {
        return new PageDto<>(
                pageModel.getItems(),
                pageModel.getPageNumber(),
                pageModel.getTotalItems(),
                pageModel.getItemsPerPage()
        );
    }

    public PageableModel toPageableModel(PaginationDto dto) {
        int pageNumber = Optional.ofNullable(dto.getPage()).map(Integer::parseInt).orElse(0);
        int itemsPerPage = Optional.ofNullable(dto.getSize()).map(Integer::parseInt).orElse(10);
        SortModel sortModel = toSortModel(dto);
        return PageableModel.of(pageNumber, itemsPerPage, sortModel);
    }

    public SortModel toSortModel(PaginationDto dto) {
        if (dto.getSort() == null || dto.getSort().isEmpty()) {
            return SortModel.empty();
        }

        SortModel.Direction direction = Optional.ofNullable(dto.getDirection())
                .map(String::toUpperCase)
                .map(SortModel.Direction::valueOf)
                .orElse(SortModel.Direction.ASC); // Default direction

        SortModel.Order order = SortModel.Order.by(dto.getSort(), direction);
        return SortModel.by(Collections.singletonList(order));
    }

    public List<Filter> toFilters(List<FilterDto> filterDtos) {
        if (filterDtos == null || filterDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return filterDtos.stream()
                .map(this::toFilter)
                .collect(Collectors.toList());
    }

    private Filter toFilter(FilterDto dto) {
        return switch (Filter.FilterType.valueOf(dto.getType())) {
            case DATE -> toFilterDate((FilterDateDto) dto);
            case RANGE -> toFilterRange((FilterRangeDto) dto);
            case STRING -> toFilterString((FilterStringDto) dto);
            case NUMBER -> toFilterNumber((FilterNumberDto) dto);
            case BOOLEAN -> toFilterBoolean((FilterBooleanDto) dto);
            case LIST -> toFilterList((FilterListDto) dto);
            default -> throw new IllegalArgumentException("Unsupported filter type: " + dto.getType());
        };
    }

    private FilterDate toFilterDate(FilterDateDto dto) {
        return FilterDate.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .filterDateType(dto.getFilterDateType())
                .build();
    }

    private FilterRange toFilterRange(FilterRangeDto dto) {
        return FilterRange.builder()
                .name(dto.getName())
                .min((int) dto.getMin())
                .max((int) dto.getMax())
                .build();
    }

    private Filter toFilterString(FilterStringDto dto) {
        return new FilterString(dto.getName(), dto.getValue());
    }

    private Filter toFilterNumber(FilterNumberDto dto) {
        return new FilterNumber(dto.getName(), dto.getValue());
    }

    private Filter toFilterBoolean(FilterBooleanDto dto) {
        return new FilterBoolean(dto.getName(), dto.getValue());
    }

    private Filter toFilterList(FilterListDto dto) {
        return new FilterList(dto.getName(), new FilterListValues(dto.getValues()));
    }
}
