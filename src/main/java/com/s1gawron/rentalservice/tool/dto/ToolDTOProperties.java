package com.s1gawron.rentalservice.tool.dto;

import java.math.BigDecimal;

public interface ToolDTOProperties {

    String getName();

    String getDescription();

    String getToolCategory();

    BigDecimal getPrice();

    ToolStateDTO getToolState();

}
