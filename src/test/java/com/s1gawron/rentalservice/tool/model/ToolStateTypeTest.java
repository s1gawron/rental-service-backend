package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolStateTypeTest {

    @Test
    void shouldFindRole() {
        final ToolStateType toolStateType = ToolStateType.findByValue("new");

        assertNotNull(toolStateType);
        assertEquals(ToolStateType.NEW, toolStateType);
    }

    @Test
    void shouldNotFindRole() {
        assertThrows(ToolStateTypeDoesNotExistException.class, () -> ToolStateType.findByValue("doesNotExist"),
            "Tool state type: doesNotExist does not exist!");
    }

}