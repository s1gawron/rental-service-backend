package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ToolStateType {

    NEW("NEW"),

    MINIMAL_WEAR("MINIMAL_WEAR"),

    WELL_WORN("WELL_WORN");

    private final String name;

    public static ToolStateType findByValue(final String type) {
        return Arrays.stream(values()).filter(value -> value.name.equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> ToolStateTypeDoesNotExistException.create(type));
    }

}
