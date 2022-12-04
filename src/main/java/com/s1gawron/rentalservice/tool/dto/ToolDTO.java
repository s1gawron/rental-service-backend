package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ToolDTO.ToolDTOBuilder.class)
public class ToolDTO implements ToolDTOProperties {

    private final Long toolId;

    private final String name;

    private final String description;

    private final ToolCategory toolCategory;

    private final BigDecimal price;

    private final ToolStateDTO toolState;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolDTOBuilder {

    }

    public static ToolDTO from(final Tool tool) {
        return new ToolDTO(tool.getToolId(), tool.getName(), tool.getDescription(), tool.getToolCategory(), tool.getPrice(),
            ToolStateDTO.from(tool.getToolState()));
    }
}
