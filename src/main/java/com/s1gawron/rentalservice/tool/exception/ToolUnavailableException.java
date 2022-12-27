package com.s1gawron.rentalservice.tool.exception;

public class ToolUnavailableException extends RuntimeException {

    private ToolUnavailableException(final String message) {
        super(message);
    }

    public static ToolUnavailableException create(final long toolId) {
        return new ToolUnavailableException("Tool#" + toolId + " is unavailable!");
    }
}
