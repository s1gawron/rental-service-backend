package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.ToolSearchDTO;
import com.s1gawron.rentalservice.tool.dto.validator.ToolDTOValidator;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetToolControllerTest extends ToolControllerTest {

    private static final String TOOL_GET_ENDPOINT = "/api/public/tool/get/";

    @Test
    void shouldGetToolsByCategory() throws Exception {
        final List<ToolDetailsDTO> heavyTools = ToolCreatorHelper.I.createToolDTOList().stream()
            .filter(tool -> tool.toolCategory().equals(ToolCategory.HEAVY.name()))
            .collect(Collectors.toList());

        Mockito.when(toolServiceMock.getToolsByCategory("heavy")).thenReturn(new ToolListingDTO(heavyTools.size(), heavyTools));

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "category/heavy");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ToolListingDTO toolListingDTOResult = objectMapper.readValue(jsonResult, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolListingDTOResult);
        assertEquals(2, toolListingDTOResult.count());
        assertEquals(2, toolListingDTOResult.tools().size());
        assertEquals(2, getToolListSizeFilteredByCategory(ToolCategory.HEAVY, toolListingDTOResult.tools()));
        assertEquals(0, getToolListSizeFilteredByCategory(ToolCategory.LIGHT, toolListingDTOResult.tools()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExist() throws Exception {
        final ToolCategoryDoesNotExistException expectedException = ToolCategoryDoesNotExistException.create("medium");
        final String endpoint = TOOL_GET_ENDPOINT + "category/medium";

        Mockito.when(toolServiceMock.getToolsByCategory("medium")).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldGetNewTools() throws Exception {
        final List<ToolDetailsDTO> tools = ToolCreatorHelper.I.createToolDTOList().stream().limit(3).collect(Collectors.toList());

        Mockito.when(toolServiceMock.getNewTools()).thenReturn(tools);

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "new");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> toolDetailsDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDetailsDTOListResult);
        assertEquals(3, toolDetailsDTOListResult.size());
        assertEquals(2, getToolListSizeFilteredByCategory(ToolCategory.HEAVY, toolDetailsDTOListResult));
        assertEquals(1, getToolListSizeFilteredByCategory(ToolCategory.LIGHT, toolDetailsDTOListResult));
    }

    @Test
    void shouldGetToolById() throws Exception {
        Mockito.when(toolServiceMock.getToolDetails(1L)).thenReturn(ToolCreatorHelper.I.createToolDetailsDTO());

        final RequestBuilder request = MockMvcRequestBuilders.get(TOOL_GET_ENDPOINT + "id/1");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ToolDetailsDTO toolDetailsDTOResult = objectMapper.readValue(jsonResult, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(ToolDTOValidator.I.validate(toolDetailsDTOResult));
        assertEquals("Hammer", toolDetailsDTOResult.name());
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() throws Exception {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final String endpoint = TOOL_GET_ENDPOINT + "id/1";

        Mockito.when(toolServiceMock.getToolDetails(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldGetToolsByName() throws Exception {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final List<ToolDetailsDTO> toolDetailsDTOList = ToolCreatorHelper.I.createCommonNameToolDTOList().stream()
            .filter(tool -> tool.name().toLowerCase().contains(toolSearchDTO.toolName()))
            .collect(Collectors.toList());

        Mockito.when(toolServiceMock.getToolsByName(Mockito.any(ToolSearchDTO.class))).thenReturn(toolDetailsDTOList);

        final String json = objectMapper.writeValueAsString(toolSearchDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").content(json).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> toolDetailsDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDetailsDTOListResult);
        assertEquals(2, toolDetailsDTOListResult.size());
    }

    @Test
    void shouldReturnEmptyListWhenToolsAreNotFoundByName() throws Exception {
        final ToolSearchDTO toolSearchDTO = new ToolSearchDTO("hammer");
        final String json = objectMapper.writeValueAsString(toolSearchDTO);
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_GET_ENDPOINT + "name").content(json).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> toolDetailsDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDetailsDTOListResult);
        assertEquals(0, toolDetailsDTOListResult.size());
    }

}
