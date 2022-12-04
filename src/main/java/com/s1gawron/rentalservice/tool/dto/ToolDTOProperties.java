package com.s1gawron.rentalservice.tool.dto;

import com.s1gawron.rentalservice.tool.model.ToolCategory;

import java.math.BigDecimal;

public interface ToolDTOProperties {

    String getName();

    String getDescription();

    ToolCategory getToolCategory();

    BigDecimal getPrice();

    ToolStateDTO getToolState();

}
