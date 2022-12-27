package com.s1gawron.rentalservice.tool.exception;

public class ToolCategoryDoesNotExistException extends RuntimeException {

    private ToolCategoryDoesNotExistException(final String message) {
        super(message);
    }

    public static ToolCategoryDoesNotExistException create(final String category) {
        return new ToolCategoryDoesNotExistException("Category#" + category + " does not exist!");
    }
}
