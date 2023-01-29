package com.s1gawron.rentalservice.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ToolListingDTOSerializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldSerialize() throws IOException {
        final List<ToolDetailsDTO> toolDetailsDTOList = ToolCreatorHelper.I.createToolDTOList();
        final ToolListingDTO toolListingDTO = new ToolListingDTO(toolDetailsDTOList.size(), toolDetailsDTOList);

        final String toolListingDTOJsonResult = mapper.writeValueAsString(toolListingDTO);
        final String expectedToolListingDTOJsonResult = Files.readString(Path.of("src/test/resources/tool-listing-dto.json"));

        final JsonNode expected = mapper.readTree(expectedToolListingDTOJsonResult);
        final JsonNode result = mapper.readTree(toolListingDTOJsonResult);

        Assertions.assertEquals(expected, result);
    }

}