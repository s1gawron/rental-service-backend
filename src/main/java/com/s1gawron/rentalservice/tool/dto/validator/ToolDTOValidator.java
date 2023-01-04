package com.s1gawron.rentalservice.tool.dto.validator;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDTOProperties;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum ToolDTOValidator {

    I;

    private static final String MESSAGE = " cannot be empty";

    public boolean validate(final ToolDTOProperties toolDTOProperties) {
        validateToolIdDuringEditOperation(toolDTOProperties);

        if (toolDTOProperties.getName() == null) {
            log.error("Tool name" + MESSAGE);
            throw ToolEmptyPropertiesException.createForName();
        }

        if (toolDTOProperties.getDescription() == null) {
            log.error("Tool description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForDescription();
        }

        if (toolDTOProperties.getToolCategory() == null) {
            log.error("Tool category" + MESSAGE);
            throw ToolEmptyPropertiesException.createForCategory();
        }

        ToolCategory.findByValue(toolDTOProperties.getToolCategory());

        if (toolDTOProperties.getPrice() == null) {
            log.error("Tool price" + MESSAGE);
            throw ToolEmptyPropertiesException.createForPrice();
        }

        if (toolDTOProperties.getToolState() == null) {
            log.error("Tool state" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolState();
        }

        if (toolDTOProperties.getToolState().getStateType() == null) {
            log.error("Tool state type" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateType();
        }

        ToolStateType.findByValue(toolDTOProperties.getToolState().getStateType());

        if (toolDTOProperties.getToolState().getDescription() == null) {
            log.error("Tool state description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateDescription();
        }

        return true;
    }

    private void validateToolIdDuringEditOperation(final ToolDTOProperties toolDTOProperties) {
        if (!(toolDTOProperties instanceof ToolDetailsDTO)) {
            return;
        }

        if (((ToolDetailsDTO) toolDTOProperties).getToolId() == null) {
            log.error("Tool id" + MESSAGE);
            throw ToolEmptyPropertiesException.createForId();
        }

        if (((ToolDetailsDTO) toolDTOProperties).getAvailable() == null) {
            log.error("Tool availability" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolAvailability();
        }
    }

}
