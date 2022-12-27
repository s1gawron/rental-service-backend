package com.s1gawron.rentalservice.tool.controller.integration;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import com.s1gawron.rentalservice.tool.model.Tool;
import lombok.SneakyThrows;
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

    private static final String TOOL_EDIT_ENDPOINT = "/api/tool/edit";

    private long currentToolId;

    private ToolDetailsDTO currentToolFirstState;

    @BeforeEach
    void setUp() {
        super.setUp();

        final Tool tool = ToolCreatorHelper.I.createTool();
        toolRepository.save(tool);
        currentToolId = tool.getToolId();
        currentToolFirstState = tool.toToolDetailsDTO();
    }

    @Test
    @SneakyThrows
    void shouldValidateAndEditTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
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
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String resultJson = result.getResponse().getContentAsString();
        final ToolDetailsDTO resultObject = objectMapper.readValue(resultJson, ToolDetailsDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(toolRepository.findById(currentToolId).isPresent());
        assertToolDetailsDTO(expectedObject, resultObject);
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToEditTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
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
            .header("Authorization", getCustomerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnNotFoundResponseWhenToolIsNotFoundWhileEditingTool() {
        final Optional<Tool> noToolInDb = toolRepository.findById(99L);

        if (noToolInDb.isPresent()) {
            throw new IllegalStateException("Tool cannot be in database, because it was not added!");
        }

        final String json = "{\n"
            + "  \"toolId\": " + 99 + ",\n"
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
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertTrue(toolRepository.findById(99L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolIdIsEmptyWhileEditingTool() {
        final String json = "{\n"
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
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"UNKNOWN\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"UNKNOWN\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"UNKNOWN\",\n"
            + "    \"description\": \"Used twice\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileEditingTool() {
        final String json = "{\n"
            + "  \"toolId\": " + currentToolId + ",\n"
            + "  \"description\": \"Bigger hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 15.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"MINIMAL_WEAR\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.put(TOOL_EDIT_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertThatToolHasNotBeenEdited();
    }

    private void assertToolDetailsDTO(final ToolDetailsDTO expected, final ToolDetailsDTO resultTool) {
        assertEquals(expected.getName(), resultTool.getName());
        assertEquals(expected.getDescription(), resultTool.getDescription());
        assertEquals(expected.getToolCategory(), resultTool.getToolCategory());
        assertEquals(expected.getPrice(), resultTool.getPrice());
        assertEquals(expected.getToolState().getStateType(), resultTool.getToolState().getStateType());
    }

    private void assertThatToolHasNotBeenEdited() {
        final Optional<Tool> toolById = toolRepository.findById(currentToolId);
        assertTrue(toolById.isPresent());
        assertToolDetailsDTO(currentToolFirstState, toolById.get().toToolDetailsDTO());
    }
}
