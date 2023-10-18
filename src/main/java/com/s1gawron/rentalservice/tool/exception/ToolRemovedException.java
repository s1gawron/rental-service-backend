package com.s1gawron.rentalservice.tool.exception;

public class ToolRemovedException extends RuntimeException {

    private ToolRemovedException(final String message) {
        super(message);
    }

    public static ToolRemovedException create(final long toolId) {
        return new ToolRemovedException("Tool#" + toolId + " is removed!");
    }

}
