package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@JsonDeserialize(builder = ToolListingDTO.ToolListingDTOBuilder.class)
public class ToolListingDTO {

    private final int count;

    private final List<ToolDetailsDTO> tools;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ToolListingDTOBuilder {

    }

    public static ToolListingDTO create(final List<ToolDetailsDTO> toolDetailsDTOS) {
        return new ToolListingDTO(toolDetailsDTOS.size(), toolDetailsDTOS);
    }

}
