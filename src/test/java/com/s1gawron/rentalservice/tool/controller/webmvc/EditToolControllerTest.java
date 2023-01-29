package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.dto.ToolStateDTO;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
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

class EditToolControllerTest extends ToolManagementControllerTest {

    private static final String TOOL_EDIT_ENDPOINT = "/api/tool/edit";

    @Test
    void shouldValidateAndEditTool() throws Exception {
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenReturn(toolDetailsDTO);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertFalse(jsonResult.isEmpty());
        assertEquals(expectedJson, jsonResult);
    }

    @Test
    void shouldReturnNotFoundResponseWhenUserIsNotFoundWhileEditingTool() throws Exception {
        final UserNotFoundException expectedException = UserNotFoundException.create("test@test.pl");
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToEditTool() throws Exception {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("TOOL MANAGEMENT");
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileEditingTool() throws Exception {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIdIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForName();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(null, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForName();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, null, "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", null, "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForCategory();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", null, BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileEditingTool() throws Exception {
        final ToolCategoryDoesNotExistException expectedException = ToolCategoryDoesNotExistException.create("UNKNOWN");
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "UNKNOWN", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForPrice();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", null, toolStateDTO, "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolState();
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), null,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateType();
        final ToolStateDTO toolStateDTO = new ToolStateDTO(null, "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileEditingTool() throws Exception {
        final ToolStateTypeDoesNotExistException expectedException = ToolStateTypeDoesNotExistException.create("UNKNOWN");
        final ToolStateDTO toolStateDTO = new ToolStateDTO("UNKNOWN", "New tool");
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileEditingTool() throws Exception {
        final ToolEmptyPropertiesException expectedException = ToolEmptyPropertiesException.createForToolStateDescription();
        final ToolStateDTO toolStateDTO = new ToolStateDTO("NEW", null);
        final ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO(1L, true, "Hammer", "Just a hammer", "LIGHT", BigDecimal.valueOf(5.99), toolStateDTO,
            "www.image.com/hammer");
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.validateAndEditTool(Mockito.any(ToolDetailsDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, expectedException.getMessage(), TOOL_EDIT_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
