package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolSearchDTODeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String userLoginJson = Files.readString(Path.of("src/test/resources/tool-search-dto.json"));
        final ToolSearchDTO result = mapper.readValue(userLoginJson, ToolSearchDTO.class);

        assertEquals("hammer", result.getToolName());
    }

}