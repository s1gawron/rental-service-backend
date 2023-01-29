package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolStateDTOSerializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldSerialize() throws IOException {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");

        final String toolStateDTOJsonResult = mapper.writeValueAsString(toolStateDTO);
        final String expectedToolStateDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-state-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolStateDTOJsonResult);
        final JsonNode result = mapper.readTree(toolStateDTOJsonResult);

        assertEquals(expected, result);
    }

    @Test
    void shouldDeserialize() throws IOException {
        final String toolStateJson = Files.readString(Path.of("src/test/resources/tool-state-dto.json"));
        final ToolStateDTO result = mapper.readValue(toolStateJson, ToolStateDTO.class);

        assertEquals(ToolStateType.NEW.name(), result.stateType());
        assertEquals("New and shiny tool", result.description());
    }

}