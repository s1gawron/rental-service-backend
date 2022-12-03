package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.ToolCategory;
import com.s1gawron.rentalservice.tool.model.ToolStateType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EditToolControllerTest extends AbstractToolControllerTest {

    private static final String TOOL_EDIT_ENDPOINT = "/api/tool/edit/1";

    @Test
    @SneakyThrows
    void shouldValidateAndEditTool() {
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenReturn(toolDTO);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertFalse(jsonResult.isEmpty());
        assertEquals(expectedJson, jsonResult);
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenUserIsNotFoundWhileEditingTool() {
        final UserNotFoundException expectedException = UserNotFoundException.create("test@test.pl");
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToEditTool() {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("TOOL MANAGEMENT");
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileEditingTool() {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForName();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO(null, "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", null, ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForCategory();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", null, BigDecimal.valueOf(5.99), toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForPrice();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, null, toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolState();
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), null);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateType();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(null, "New");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileEditingTool() {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(ToolStateType.NEW, null);
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", ToolCategory.LIGHT, BigDecimal.valueOf(5.99), toolStateDTO);
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.eq(1L), Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
