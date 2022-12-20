package com.s1gawron.rentalservice.tool.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
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
import org.springframework.http.MediaType;
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
        final List<ToolDetailsDTO> heavyTools = ToolCreatorHelper.I.createToolDTOList().stream()
            .filter(tool -> tool.getToolCategory().equals(ToolCategory.HEAVY.getName()))
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
        final List<ToolDetailsDTO> tools = ToolCreatorHelper.I.createToolDTOList().stream().limit(3).collect(Collectors.toList());

        Mockito.when(toolServiceMock.getNewTools()).thenReturn(tools);

        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tool/get/new");
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
    @SneakyThrows
    void shouldGetToolById() {
        Mockito.when(toolServiceMock.getToolDetails(1L)).thenReturn(ToolCreatorHelper.I.createToolDetailsDTO());

        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tool/get/id/1");
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final ToolDetailsDTO toolDetailsDTOResult = objectMapper.readValue(jsonResult, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(ToolDTOValidator.I.validate(toolDetailsDTOResult));
        assertEquals("Hammer", toolDetailsDTOResult.getName());
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundById() {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final String endpoint = "/api/tool/get/id/1";

        Mockito.when(toolServiceMock.getToolDetails(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.get(endpoint);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), endpoint, toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldGetToolsByName() {
        final String toolName = "hammer";
        final List<ToolDetailsDTO> toolDetailsDTOList = ToolCreatorHelper.I.createCommonNameToolDTOList().stream()
            .filter(tool -> tool.getName().toLowerCase().contains(toolName))
            .collect(Collectors.toList());

        Mockito.when(toolServiceMock.getToolsByName(toolName)).thenReturn(toolDetailsDTOList);

        final RequestBuilder request = MockMvcRequestBuilders.post("/api/tool/get/name").content("hammer").contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> toolDetailsDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDetailsDTOListResult);
        assertEquals(2, toolDetailsDTOListResult.size());
    }

    @Test
    @SneakyThrows
    void shouldReturnEmptyListWhenToolsAreNotFoundByName() {
        final RequestBuilder request = MockMvcRequestBuilders.post("/api/tool/get/name").content("hammer").contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final List<ToolDetailsDTO> toolDetailsDTOListResult = objectMapper.readValue(jsonResult, new TypeReference<>() {

        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(toolDetailsDTOListResult);
        assertEquals(0, toolDetailsDTOListResult.size());
    }

}
