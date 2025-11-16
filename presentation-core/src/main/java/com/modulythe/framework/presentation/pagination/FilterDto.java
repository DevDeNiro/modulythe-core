package com.modulythe.framework.presentation.pagination;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FilterDateDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = FilterRangeDto.class, name = "RANGE"),
        @JsonSubTypes.Type(value = FilterStringDto.class, name = "STRING"),
        @JsonSubTypes.Type(value = FilterListDto.class, name = "LIST"),
        @JsonSubTypes.Type(value = FilterBooleanDto.class, name = "BOOLEAN"),
        @JsonSubTypes.Type(value = FilterNumberDto.class, name = "NUMBER")
})
public abstract class FilterDto {
    private String name;
    private String type;

    protected FilterDto() {
        // Default constructor for Jackson
    }

    protected FilterDto(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

