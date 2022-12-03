package com.s1gawron.rentalservice.tool.dto.validator;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum ToolDTOValidator {

    I;

    private static final String MESSAGE = " cannot be empty";

    public boolean validate(final ToolDTO toolDTO) {
        if (toolDTO.getName() == null) {
            log.error("Tool name" + MESSAGE);
            throw ToolEmptyPropertiesException.createForName();
        }

        if (toolDTO.getDescription() == null) {
            log.error("Tool description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForDescription();
        }

        if (toolDTO.getToolCategory() == null) {
            log.error("Tool category" + MESSAGE);
            throw ToolEmptyPropertiesException.createForCategory();
        }

        if (toolDTO.getPrice() == null) {
            log.error("Tool price" + MESSAGE);
            throw ToolEmptyPropertiesException.createForPrice();
        }

        if (toolDTO.getToolState() == null) {
            log.error("Tool state" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolState();
        }

        if (toolDTO.getToolState().getStateType() == null) {
            log.error("Tool state type" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateType();
        }

        if (toolDTO.getToolState().getDescription() == null) {
            log.error("Tool state description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateDescription();
        }

        return true;
    }

}
