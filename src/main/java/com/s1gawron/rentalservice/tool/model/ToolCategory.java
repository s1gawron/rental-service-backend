package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ToolCategory {

    LIGHT("LIGHT"),

    HEAVY("HEAVY");

    private final String name;

    public static ToolCategory findByValue(final String category) {
        return Arrays.stream(values()).filter(value -> value.name.equalsIgnoreCase(category))
            .findFirst()
            .orElseThrow(() -> ToolCategoryDoesNotExistException.create(category));
    }
}
