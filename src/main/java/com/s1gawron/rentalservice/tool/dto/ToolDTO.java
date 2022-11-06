package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.model.ToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ToolDTO.ToolDTOBuilder.class)
public class ToolDTO {

    private final String name;

    private final String description;

    private final ToolType toolType;

    private final BigDecimal price;

    private final ToolState toolState;

    private final int quantity;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolDTOBuilder {

    }

    public static ToolDTO from(final Tool tool, final int quantity) {
        return new ToolDTO(tool.getName(), tool.getDescription(), tool.getToolType(), tool.getPrice(), tool.getToolState(), quantity);
    }
}
