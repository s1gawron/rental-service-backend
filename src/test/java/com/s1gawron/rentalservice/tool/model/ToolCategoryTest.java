package com.s1gawron.rentalservice.tool.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ToolCategoryTest {

    @Test
    void shouldFindCategory() {
        final Optional<ToolCategory> toolCategory = ToolCategory.findByValue("heavy");

        assertTrue(toolCategory.isPresent());
        assertEquals(ToolCategory.HEAVY, toolCategory.get());
    }

    @Test
    void shouldNotFindCategory() {
        final Optional<ToolCategory> toolCategory = ToolCategory.findByValue("doesNotExist");

        assertTrue(toolCategory.isEmpty());
    }

}