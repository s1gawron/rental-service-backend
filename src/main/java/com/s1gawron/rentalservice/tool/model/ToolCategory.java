package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;

import java.util.Arrays;

public enum ToolCategory {

    LIGHT,

    HEAVY;

    public static ToolCategory findByValue(final String category) {
        return Arrays.stream(values()).filter(value -> value.name().equalsIgnoreCase(category))
            .findFirst()
            .orElseThrow(() -> ToolCategoryDoesNotExistException.create(category));
    }
}
