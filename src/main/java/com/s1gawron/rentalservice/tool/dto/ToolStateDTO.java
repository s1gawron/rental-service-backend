package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ToolStateDTO.ToolStateDTOBuilder.class)
public class ToolStateDTO {

    private final ToolStateType stateType;

    private final String description;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolStateDTOBuilder {

    }

    public static ToolStateDTO from(final ToolState toolState) {
        return new ToolStateDTO(toolState.getStateType(), toolState.getDescription());
    }

}
