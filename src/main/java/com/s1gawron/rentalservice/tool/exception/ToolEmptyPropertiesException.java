package com.s1gawron.rentalservice.tool.exception;

public class ToolEmptyPropertiesException extends RuntimeException {

    private ToolEmptyPropertiesException(final String message) {
        super(message);
    }

    public static ToolEmptyPropertiesException createForId() {
        return new ToolEmptyPropertiesException("Tool id cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForName() {
        return new ToolEmptyPropertiesException("Tool name cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForDescription() {
        return new ToolEmptyPropertiesException("Tool description cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForCategory() {
        return new ToolEmptyPropertiesException("Tool category cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForPrice() {
        return new ToolEmptyPropertiesException("Tool price cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForToolState() {
        return new ToolEmptyPropertiesException("Tool state cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForToolStateType() {
        return new ToolEmptyPropertiesException("Tool state type cannot be empty!");
    }

    public static ToolEmptyPropertiesException createForToolStateDescription() {
        return new ToolEmptyPropertiesException("Tool state description cannot be empty!");
    }
}
