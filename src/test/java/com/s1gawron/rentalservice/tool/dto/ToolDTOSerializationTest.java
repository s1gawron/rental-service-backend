package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");
        final ToolDTO toolDTO = new ToolDTO(1L, "Hammer", "It's just a hammer :)", "LIGHT", BigDecimal.valueOf(10.99), toolStateDTO);

        final String toolDTOJsonResult = mapper.writeValueAsString(toolDTO);
        final String expectedToolDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOJsonResult);

        Assertions.assertEquals(expected, result);
    }

    @Test
    @SneakyThrows
    void shouldSerializeList() {
        final List<ToolDTO> toolDTOList = ToolCreatorHelper.I.createToolDTOList();
        final String toolDTOListJsonResult = mapper.writeValueAsString(toolDTOList);
        final String expectedToolDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-dto-list.json"));

        final JsonNode expected = mapper.readTree(expectedToolDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOListJsonResult);

        Assertions.assertEquals(expected, result);
    }

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String toolJson = Files.readString(Path.of("src/test/resources/tool-dto.json"));
        final ToolDTO result = mapper.readValue(toolJson, ToolDTO.class);

        assertEquals("Hammer", result.getName());
        assertEquals("It's just a hammer :)", result.getDescription());
        assertEquals(ToolCategory.LIGHT.getName(), result.getToolCategory());
        assertEquals(BigDecimal.valueOf(10.99), result.getPrice());
        assertEquals(ToolStateType.NEW.getName(), result.getToolState().getStateType());
        assertEquals("New and shiny tool", result.getToolState().getDescription());
    }

}