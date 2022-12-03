package com.s1gawron.rentalservice.tool.model;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum ToolCategory {

    LIGHT("light"),

    HEAVY("heavy");

    private final String name;

    public static Optional<ToolCategory> findByValue(final String category) {
        return Arrays.stream(values()).filter(value -> value.name.equalsIgnoreCase(category)).findFirst();
    }
}
