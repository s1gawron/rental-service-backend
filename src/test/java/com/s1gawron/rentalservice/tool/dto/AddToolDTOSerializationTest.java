package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddToolDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");
        final AddToolDTO addToolDTO = new AddToolDTO("Hammer", "It's just a hammer :)", "LIGHT", BigDecimal.valueOf(10.99), toolStateDTO);

        final String addToolDTOJsonResult = mapper.writeValueAsString(addToolDTO);
        final String expectedAddToolDTOJsonResult = Files.readString(Path.of("src/test/resources/add-tool-dto.json"));

        final JsonNode expected = mapper.readTree(expectedAddToolDTOJsonResult);
        final JsonNode result = mapper.readTree(addToolDTOJsonResult);

        assertEquals(expected, result);
    }

}