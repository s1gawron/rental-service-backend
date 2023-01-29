package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolSearchDTODeserializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldDeserialize() throws IOException {
        final String userLoginJson = Files.readString(Path.of("src/test/resources/tool-search-dto.json"));
        final ToolSearchDTO result = mapper.readValue(userLoginJson, ToolSearchDTO.class);

        assertEquals("hammer", result.toolName());
    }

}