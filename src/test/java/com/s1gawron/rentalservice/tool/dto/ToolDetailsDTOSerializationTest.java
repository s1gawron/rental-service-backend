package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolDetailsDTOSerializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldSerialize() throws IOException {
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New and shiny tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, false, "Hammer", "It's just a hammer :)", "LIGHT", BigDecimal.valueOf(10.99),
            toolStateDTO, "www.image.com/hammer");

        final String toolDTOJsonResult = mapper.writeValueAsString(toolDetailsDTO);
        final String expectedToolDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-details-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOJsonResult);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldSerializeList() throws IOException {
        final List<ToolDetailsDTO> toolDetailsDTOList = ToolCreatorHelper.I.createToolDTOList();
        final String toolDTOListJsonResult = mapper.writeValueAsString(toolDetailsDTOList);
        final String expectedToolDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-details-dto-list.json"));

        final JsonNode expected = mapper.readTree(expectedToolDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOListJsonResult);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldDeserialize() throws IOException {
        final String toolJson = Files.readString(Path.of("src/test/resources/tool-details-dto.json"));
        final ToolDetailsDTO result = mapper.readValue(toolJson, ToolDetailsDTO.class);

        assertEquals("Hammer", result.name());
        assertEquals("It's just a hammer :)", result.description());
        assertEquals(ToolCategory.LIGHT.name(), result.toolCategory());
        assertEquals(BigDecimal.valueOf(10.99), result.price());
        assertEquals(ToolStateType.NEW.name(), result.toolState().stateType());
        assertEquals("New and shiny tool", result.toolState().description());
        assertEquals("www.image.com/hammer", result.imageUrl());
    }

}