package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ToolSearchDTO.ToolSearchDTOBuilder.class)
public class ToolSearchDTO {

    private final String toolName;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolSearchDTOBuilder {

    }

}
