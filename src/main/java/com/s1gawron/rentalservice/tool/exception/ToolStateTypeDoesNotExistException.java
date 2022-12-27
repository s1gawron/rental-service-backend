package com.s1gawron.rentalservice.tool.exception;

public class ToolStateTypeDoesNotExistException extends RuntimeException {

    private ToolStateTypeDoesNotExistException(final String message) {
        super(message);
    }

    public static ToolStateTypeDoesNotExistException create(final String toolStateType) {
        return new ToolStateTypeDoesNotExistException("Tool state type#" + toolStateType + " does not exist!");
    }
}
