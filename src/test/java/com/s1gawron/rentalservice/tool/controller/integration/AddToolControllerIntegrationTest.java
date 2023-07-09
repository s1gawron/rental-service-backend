package com.s1gawron.rentalservice.tool.controller.integration;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_ADD_ENDPOINT = "/api/management/tool/add";

    @Test
    void shouldValidateAndAddTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final ToolDetailsDTO expectedObject = objectMapper.readValue(json, ToolDetailsDTO.class);
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(1, toolDAO.findAll().size());
        assertToolDetailsDTO(expectedObject, resultObject, true);
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToAddTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.CUSTOMER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAuthenticated() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "UNKNOWN",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "toolState": {
                "stateType": "NEW",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "UNKNOWN",
                "description": "New and shiny tool"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileAddingTool() throws Exception {
        final String json = """
            {
              "name": "Hammer",
              "description": "It's just a hammer :)",
              "toolCategory": "LIGHT",
              "price": 10.99,
              "toolState": {
                "stateType": "NEW"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertEquals(0, toolDAO.findAll().size());
    }

    private void assertToolDetailsDTO(final ToolDetailsDTO expected, final ToolDetailsDTO resultTool, final boolean isAvailable) {
        assertEquals(expected.name(), resultTool.name());
        assertEquals(isAvailable, resultTool.available());
        assertEquals(expected.description(), resultTool.description());
        assertEquals(expected.toolCategory(), resultTool.toolCategory());
        assertEquals(expected.price(), resultTool.price());
        assertEquals(expected.toolState().stateType(), resultTool.toolState().stateType());
    }

}
