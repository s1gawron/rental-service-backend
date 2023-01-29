package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;

import java.util.Arrays;

public enum ToolStateType {

    NEW,

    MINIMAL_WEAR,

    WELL_WORN;

    public static ToolStateType findByValue(final String type) {
        return Arrays.stream(values()).filter(value -> value.name().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> ToolStateTypeDoesNotExistException.create(type));
    }

}
