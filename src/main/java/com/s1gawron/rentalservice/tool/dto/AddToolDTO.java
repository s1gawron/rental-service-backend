package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = AddToolDTO.AddToolDTOBuilder.class)
public class AddToolDTO implements ToolDTOProperties {

    private final String name;

    private final String description;

    private final ToolCategory toolCategory;

    private final BigDecimal price;

    private final ToolStateDTO toolState;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddToolDTOBuilder {

    }

}
