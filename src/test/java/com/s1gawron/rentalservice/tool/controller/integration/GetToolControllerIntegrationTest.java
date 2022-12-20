package com.s1gawron.rentalservice.tool.controller.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolState;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GetToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_GET_ENDPOINT = "/api/public/tool/get/";

    @Test
    @SneakyThrows
    void shouldGetToolsByCategory() {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/heavy");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolListingDTO resultObject = objectMapper.readValue(resultJson, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultObject.getCount());
        assertEquals(3, resultObject.getTools().size());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExist() {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/unknown");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    void shouldGetNewTools() {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "new");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(3, resultList.size());
    }

    @Test
    @SneakyThrows
    void shouldGetToolById() {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());
        final Tool chainsaw = createChainsaw();
        toolRepository.save(chainsaw);

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/" + chainsaw.getToolId());

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(chainsaw.getToolId(), resultObject.getToolId());
        assertEquals(chainsaw.getName(), resultObject.getName());
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() {
        toolRepository.saveAll(ToolCreatorHelper.I.createHeavyTools());
        toolRepository.saveAll(ToolCreatorHelper.I.createLightTools());

        final Optional<Tool> toolById = toolService.getToolById(99L);

        if (toolById.isPresent()) {
            throw new IllegalStateException("Tool cannot be in database, because it was not added!");
        }

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/99");

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    void shouldGetToolsByName() {
        toolRepository.saveAll(ToolCreatorHelper.I.createCommonNameToolList());
        final Tool chainsaw = createChainsaw();
        toolRepository.save(chainsaw);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").content("hammer");

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> resultList = objectMapper.readValue(resultJson, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(2, resultList.size());

        for (final ToolDetailsDTO details : resultList) {
            assertTrue(details.getName().toLowerCase().contains("hammer"));
        }
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolsAreNotFoundByName() {
        toolRepository.saveAll(ToolCreatorHelper.I.createCommonNameToolList());

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").content("chainsaw").contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    private Tool createChainsaw() {
        final ToolStateDTO newState = new ToolStateDTO(ToolStateType.NEW.getName(), "New and shiny tool");
        final ToolDTO chainsawDTO = new ToolDTO("Chainsaw", "Do you want to cut a big tree?", ToolCategory.LIGHT.getName(), BigDecimal.valueOf(100.99),
            newState);

        return Tool.from(chainsawDTO, ToolState.from(newState));
    }

}
