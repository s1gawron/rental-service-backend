package com.s1gawron.rentalservice.tool.controller.webmvc;

import com.s1gawron.rentalservice.shared.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteToolControllerTest extends ToolManagementControllerTest {

    private static final String TOOL_DELETE_ENDPOINT = "/api/tool/delete/1";

    @Test
    @SneakyThrows
    void shouldDeleteTool() {
        Mockito.when(toolServiceMock.deleteTool(1L)).thenReturn(true);

        final RequestBuilder request = MockMvcRequestBuilders.delete(TOOL_DELETE_ENDPOINT);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();

        assertTrue(Boolean.parseBoolean(jsonResult));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenUserIsNotFoundWhileDeletingTool() {
        final UserNotFoundException expectedException = UserNotFoundException.create("test@test.pl");
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.deleteTool(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.delete(TOOL_DELETE_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_DELETE_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToDeleteTool() {
        final NoAccessForUserRoleException expectedException = NoAccessForUserRoleException.create("TOOL MANAGEMENT");
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.deleteTool(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.delete(TOOL_DELETE_ENDPOINT).content(expectedJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.FORBIDDEN, expectedException.getMessage(), TOOL_DELETE_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileDeletingTool() {
        final ToolNotFoundException expectedException = ToolNotFoundException.create(1L);
        final ToolDetailsDTO toolDetailsDTO = ToolCreatorHelper.I.createToolDetailsDTO();
        final String expectedJson = objectMapper.writeValueAsString(toolDetailsDTO);

        Mockito.when(toolServiceMock.deleteTool(1L)).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.delete(TOOL_DELETE_ENDPOINT).content(expectedJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.NOT_FOUND, expectedException.getMessage(), TOOL_DELETE_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}
