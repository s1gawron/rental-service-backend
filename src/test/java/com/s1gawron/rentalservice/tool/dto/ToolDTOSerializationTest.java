package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "It's just a hammer :)", "LIGHT", BigDecimal.valueOf(10.99), toolStateDTO, "www.image.com/hammer");

        final String toolDTOJsonResult = mapper.writeValueAsString(toolDTO);
        final String expectedToolDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOJsonResult);

        assertEquals(expected, result);
    }

}