package com.s1gawron.rentalservice.tool.dto;

import java.math.BigDecimal;

public record ToolDetailsDTO(Long toolId, Boolean available, String name, String description, String toolCategory, BigDecimal price, ToolStateDTO toolState,
                             String imageUrl) implements ToolDTOProperties {

}
