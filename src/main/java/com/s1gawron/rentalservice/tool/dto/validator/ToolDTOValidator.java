package com.s1gawron.rentalservice.tool.dto.validator;

import com.s1gawron.rentalservice.tool.dto.ToolDTOProperties;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ToolDTOValidator {

    I;

    private static final Logger log = LoggerFactory.getLogger(ToolDTOValidator.class);

    private static final String MESSAGE = " cannot be empty";

    public boolean validate(final ToolDTOProperties toolDTOProperties) {
        validateToolIdDuringEditOperation(toolDTOProperties);

        if (toolDTOProperties.name() == null) {
            log.error("Tool name" + MESSAGE);
            throw ToolEmptyPropertiesException.createForName();
        }

        if (toolDTOProperties.description() == null) {
            log.error("Tool description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForDescription();
        }

        if (toolDTOProperties.toolCategory() == null) {
            log.error("Tool category" + MESSAGE);
            throw ToolEmptyPropertiesException.createForCategory();
        }

        ToolCategory.findByValue(toolDTOProperties.toolCategory());

        if (toolDTOProperties.price() == null) {
            log.error("Tool price" + MESSAGE);
            throw ToolEmptyPropertiesException.createForPrice();
        }

        if (toolDTOProperties.toolState() == null) {
            log.error("Tool state" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolState();
        }

        if (toolDTOProperties.toolState().stateType() == null) {
            log.error("Tool state type" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateType();
        }

        ToolStateType.findByValue(toolDTOProperties.toolState().stateType());

        if (toolDTOProperties.toolState().description() == null) {
            log.error("Tool state description" + MESSAGE);
            throw ToolEmptyPropertiesException.createForToolStateDescription();
        }

        return true;
    }

    private void validateToolIdDuringEditOperation(final ToolDTOProperties toolDTOProperties) {
        if (!(toolDTOProperties instanceof ToolDetailsDTO)) {
            return;
        }

        if (((ToolDetailsDTO) toolDTOProperties).toolId() == null) {
            log.error("Tool id" + MESSAGE);
            throw ToolEmptyPropertiesException.createForId();
        }
    }

}
