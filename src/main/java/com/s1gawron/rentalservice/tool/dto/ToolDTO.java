package com.s1gawron.rentalservice.tool.dto;

import java.math.BigDecimal;

public record ToolDTO(String name, String description, String toolCategory, BigDecimal price, ToolStateDTO toolState, String imageUrl)
    implements ToolDTOProperties {

}
