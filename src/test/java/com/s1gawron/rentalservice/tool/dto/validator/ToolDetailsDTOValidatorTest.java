package com.s1gawron.rentalservice.tool.dto.validator;

import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolDetailsDTOValidatorTest {

    @Test
    void shouldValidateToolDTODuringEditOperation() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertTrue(ToolDTOValidator.I.validate(toolDetailsDTO));
    }

    @Test
    void shouldThrowExceptionWhenIdIsNullDuringEditOperation() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(null, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDetailsDTO), "Tool id cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolAvailabilityIsNullDuringEditOperation() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, null, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDetailsDTO), "Tool availability cannot be empty!");
    }

    @Test
    void shouldValidateToolDTO() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertTrue(ToolDTOValidator.I.validate(toolDTO));
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO(null, "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", null, "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool description cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolCategoryIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", null, BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool category cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolCategoryDoesNotExist() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "UNKNOWN", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolCategoryDoesNotExistException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Category: UNKNOWN does not exist!");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", null, toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool price cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateIsNull() {
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), null);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateTypeIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO(null, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state type cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateTypeDoesNotExist() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("UNKNOWN", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolStateTypeDoesNotExistException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state type: UNKNOWN does not exist!");
    }

    @Test
    void shouldThrowExceptionWhenToolStateDescriptionIsNull() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", null);
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO);

        assertThrows(ToolEmptyPropertiesException.class, () -> ToolDTOValidator.I.validate(toolDTO), "Tool state description cannot be empty!");
    }

}