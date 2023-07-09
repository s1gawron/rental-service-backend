package com.s1gawron.rentalservice.tool.controller.integration;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_EDIT_ENDPOINT = "/api/management/tool/edit";

    private long currentToolId;

    private ToolDetailsDTO currentToolFirstState;

    @BeforeEach
    void setUp() {
        super.setUp();

        final Tool tool = ToolCreatorHelper.I.createTool();
        saveToolForTest(tool);
        currentToolId = tool.getToolId();
        currentToolFirstState = tool.toToolDetailsDTO();
    }

    @Test
    void shouldValidateAndEditTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"available\": \"false\",\n"
            + "  \"removed\": \"true\",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final ToolDetailsDTO expectedObject = objectMapper.readValue(json, ToolDetailsDTO.class);
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(toolDAO.findById(currentToolId).isPresent());
        assertToolDetailsDTO(expectedObject, resultObject);
    }

    @Test
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToEditTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"available\": \"false\",\n"
            + "  \"removed\": \"true\",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.CUSTOMER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileEditingTool() throws Exception {
        final Optional<Tool> noToolInDb = toolDAO.findById(99L);

        if (noToolInDb.isPresent()) {
            throw new IllegalStateException("Tool cannot be in database, because it was not added!");
        }

        final String json = """
            {
              "toolId": 99,
              "available": "true",
              "removed": "false",
              "name": "Big hammer",
              "description": "Bigger hammer",
              "toolCategory": "LIGHT",
              "price": 15.99,
              "toolState": {
                "stateType": "MINIMAL_WEAR",
                "description": "Used twice"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertTrue(toolDAO.findById(99L).isEmpty());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolIdIsEmptyWhileEditingTool() throws Exception {
        final String json = """
            {
              "name": "Big hammer",
              "available": "true",
              "removed": "false",
              "description": "Bigger hammer",
              "toolCategory": "LIGHT",
              "price": 15.99,
              "toolState": {
                "stateType": "MINIMAL_WEAR",
                "description": "Used twice"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"UNKNOWN\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"UNKNOWN\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"UNKNOWN\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileEditingTool() throws Exception {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"name\": \"Big hammer\",\n"
            + "  \"available\": \"true\",\n"
            + "  \"removed\": \"false\",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationToken(UserRole.WORKER));

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    private void assertToolDetailsDTO(final ToolDetailsDTO expected, final ToolDetailsDTO resultTool) {
        assertEquals(expected.name(), resultTool.name());
        assertEquals(expected.available(), resultTool.available());
        assertEquals(expected.description(), resultTool.description());
        assertEquals(expected.toolCategory(), resultTool.toolCategory());
        assertEquals(expected.price(), resultTool.price());
        assertEquals(expected.toolState().stateType(), resultTool.toolState().stateType());
    }

    private void assertThatToolHasNotBeenEdited() {
        final Optional<Tool> toolById = toolDAO.findById(currentToolId);
        assertTrue(toolById.isPresent());
        assertToolDetailsDTO(currentToolFirstState, toolById.get().toToolDetailsDTO());
    }
}
