package com.s1gawron.rentalservice.tool.dto;

import java.math.BigDecimal;

public interface ToolDTOProperties {

    String name();

    String description();

    String toolCategory();

    BigDecimal price();

    ToolStateDTO toolState();

}
