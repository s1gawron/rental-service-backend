package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ToolDetailsDTO.ToolDetailsDTOBuilder.class)
public class ToolDetailsDTO implements ToolDTOProperties {

    private final Long toolId;

    private final Boolean available;

    private final String name;

    private final String description;

    private final String toolCategory;

    private final BigDecimal price;

    private final ToolStateDTO toolState;

    private final String imageUrl;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolDetailsDTOBuilder {

    }
}
