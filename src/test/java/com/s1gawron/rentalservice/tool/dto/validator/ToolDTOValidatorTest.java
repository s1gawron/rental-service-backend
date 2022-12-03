package com.s1gawron.rentalservice.tool.dto.validator;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolDTOValidatorTest {

    @Test
    void shouldValidate() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);

        assertTrue(ToolDTOValidator.I.validate(toolDTO));
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO(null, "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", null, ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool description cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolCategoryIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", null, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool category cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, null, toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool price cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateIsNull() {
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), null);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateTypeIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(null, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state type cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateDescriptionIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, null);
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state description cannot be empty!");
    }

}