package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_GET_ENDPOINT = "/api/public/tool/get/";

    @Test
    void shouldGetToolsByCategory() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/heavy");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultObject = objectMapper.readValue(resultJson, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultObject.count());
        assertEquals(3, resultObject.tools().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExist() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/unknown");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldGetNewTools() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyToolsWithDate());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightToolsWithDate());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "new");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultList.size());

        final long areProperToolsInResultListCount = resultList.stream().filter(tool -> {
                final String toolName = tool.name();

                return toolName.equals("Loader") || toolName.equals("Crane") || toolName.equals("Big hammer");
            })
            .count();

        assertEquals(3, areProperToolsInResultListCount);
    }

    @Test
    void shouldGetToolById() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());
        final Tool chainsaw = ToolCreatorHelper.I.createChainsaw();
        toolRepository.save(chainsaw);

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/" + chainsaw.getToolId());

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(chainsaw.getToolId(), resultObject.toolId());
        assertEquals(chainsaw.getName(), resultObject.name());
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final Optional<Tool> toolById = toolRepository.findById(99L);

        if (toolById.isPresent()) {
            throw new IllegalStateException("Tool cannot be in database, because it was not added!");
        }

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/99");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldGetToolsByName() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createCommonNameToolList());
        toolRepository.save(ToolCreatorHelper.I.createChainsaw());

        final String json = """
            {
              "toolName": "hammer"
            }""";

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(2, resultList.size());

        for (final ToolDetailsDTO details : resultList) {
            assertTrue(details.name().toLowerCase().contains("hammer"));
        }
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolsAreNotFoundByName() throws Exception {
        toolRepository.saveAll(ToolCreatorHelper.I.createCommonNameToolList());

        final String json = """
            {
              "toolName": "chainsaw"
            }""";

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").contentType(MediaType.APPLICATION_JSON).content(json);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

}
