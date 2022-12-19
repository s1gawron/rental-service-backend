package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolStateDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");

        final String toolStateDTOJsonResult = mapper.writeValueAsString(toolStateDTO);
        final String expectedToolStateDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-state-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolStateDTOJsonResult);
        final JsonNode result = mapper.readTree(toolStateDTOJsonResult);

        assertEquals(expected, result);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String toolStateJson = Files.readString(Path.of("src/test/resources/tool-state-dto.json"));
        final ToolStateDTO result = mapper.readValue(toolStateJson, ToolStateDTO.class);

        assertEquals(ToolStateType.NEW.getName(), result.getStateType());
        assertEquals("New and shiny tool", result.getDescription());
    }

}