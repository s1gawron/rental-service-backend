package com.s1gawron.rentalservice.tool.exception;

public class ToolNotFoundException extends RuntimeException {

    private ToolNotFoundException(final String message) {
        super(message);
    }

    public static ToolNotFoundException create(final Long toolId) {
        return new ToolNotFoundException("Tool: " + toolId + " could not be found!");
    }

    public static ToolNotFoundException createForName(final String name) {
        return new ToolNotFoundException("No tools were found by name: " + name);
    }
}
