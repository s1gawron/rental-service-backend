package com.s1gawron.rentalservice.tool.model;

import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolCategoryTest {

    @Test
    void shouldFindCategory() {
        final ToolCategory toolCategory = ToolCategory.findByValue("heavy");

        assertNotNull(toolCategory);
        assertEquals(ToolCategory.HEAVY, toolCategory);
    }

    @Test
    void shouldNotFindCategory() {
        assertThrows(ToolCategoryDoesNotExistException.class, () -> ToolCategory.findByValue("doesNotExist"), "Category: doesNotExist does not exist!");
    }

}