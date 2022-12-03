package com.s1gawron.rentalservice.tool.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolListingDTO;
import com.s1gawron.rentalservice.tool.dto.validator.ToolDTOValidator;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetToolControllerTest extends AbstractToolControllerTest {

    @Test
    @SneakyThrows
    void shouldGetToolsByCategory() {
        final List<ToolDTO> heavyTools = ToolCreatorHelper.I.createToolDTOList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY))
            .collect(Collectors.toList());

        Mockito.when(toolServiceMock.getToolsByCategory("heavy")).thenReturn(ToolListingDTO.create(heavyTools));

        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tool/get/category/heavy");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ToolListingDTO toolListingDTOResult = objectMapper.readValue(jsonResult, ToolListingDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolListingDTOResult);
        assertEquals(2, toolListingDTOResult.getCount());
        assertEquals(2, toolListingDTOResult.getTools().size());
        assertEquals(2, getToolListSizeFilteredByCategory(ToolCategory.HEAVY, toolListingDTOResult.getTools()));
        assertEquals(0, getToolListSizeFilteredByCategory(ToolCategory.LIGHT, toolListingDTOResult.getTools()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExist() {
        final ToolCategoryDoesNotExistException expectedException = ToolCategoryDoesNotExistException.create("medium");
        final String endpoint = "/api/tool/get/category/medium";

        Mockito.when(toolServiceMock.getToolsByCategory("medium")).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldGetNewTools() {
        final List<ToolDTO> tools = ToolCreatorHelper.I.createToolDTOList().stream().limit(3).collect(Collectors.toList());

        Mockito.when(toolServiceMock.getNewTools()).thenReturn(tools);

        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tool/get/new");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDTO> toolDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDTOListResult);
        assertEquals(3, toolDTOListResult.size());
        assertEquals(2, getToolListSizeFilteredByCategory(ToolCategory.HEAVY, toolDTOListResult));
        assertEquals(1, getToolListSizeFilteredByCategory(ToolCategory.LIGHT, toolDTOListResult));
    }

    @Test
    @SneakyThrows
    void shouldGetToolById() {
        Mockito.when(toolServiceMock.getToolById(1L)).thenReturn(ToolCreatorHelper.I.createToolDTO());

        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tool/get/id/1");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ToolDTO toolDTOResult = objectMapper.readValue(jsonResult, ToolDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(ToolDTOValidator.I.validate(toolDTOResult));
        assertEquals("Hammer", toolDTOResult.getName());
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final String endpoint = "/api/tool/get/id/1";

        Mockito.when(toolServiceMock.getToolById(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

}
