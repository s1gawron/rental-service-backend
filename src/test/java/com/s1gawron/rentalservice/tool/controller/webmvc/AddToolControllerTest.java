package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddToolControllerTest extends ToolManagementControllerTest {

    private static final String TOOL_ADD_ENDPOINT = "/api/tool/add";

    @Test
    void shouldValidateAndAddTool() throws Exception {
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String toolDTOJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenReturn(toolDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(toolDTOJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);
        final String jsonResult = result.getResponse().getContentAsString();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertFalse(jsonResult.isEmpty());
        assertEquals(expectedJson, jsonResult);
    }

    @Test
    void shouldReturnNotFoundResponseWhenUserIsNotFoundWhileAddingTool() throws Exception {
        final UserNotFoundException expectedException = UserNotFoundException.create("test@test.pl");
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToAddTool() throws Exception {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("TOOL MANAGEMENT");
        final ToolDTO toolDTO = ToolCreatorHelper.I.createToolDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isForbidden())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForName();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO(null, "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", null, "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForCategory();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", null, BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileAddingTool() throws Exception {
        final ToolCategoryDoesNotExistException expectedException = ToolCategoryDoesNotExistException.create("UNKNOWN");
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "UNKNOWN", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForPrice();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", null, toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolState();
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), null, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateType();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(null, "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileAddingTool() throws Exception {
        final ToolStateTypeDoesNotExistException expectedException = ToolStateTypeDoesNotExistException.create("UNKNOWN");
        final ToolStateDTO toolStateDTO = new ToolStateDTO("UNKNOWN", "New tool");
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileAddingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", null);
        final ToolDTO toolDTO = new ToolDTO("Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDTO);

        Mockito.when(toolServiceMock.validateAndAddTool(Mockito.any(ToolDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

}