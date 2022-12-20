package com.s1gawron.rentalservice.tool.controller.integration;

import com.s1gawron.rentalservice.tool.controller.integration.AbstractToolControllerIntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddToolControllerIntegrationTest extends AbstractToolControllerIntegrationTest {

    private static final String TOOL_ADD_ENDPOINT = "/api/tool/add";

    @Test
    @SneakyThrows
    void shouldValidateAndAddTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isPresent());
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenResponseWhenUserIsNotAllowedToAddTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getCustomerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolNameIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolDescriptionIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolCategoryDoesNotExistWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"UNKNOWN\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolPriceIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateTypeIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateTypeDoesNotExistWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"UNKNOWN\",\n"
            + "    \"description\": \"New and shiny tool\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenToolStateDescriptionIsEmptyWhileAddingTool() {
        final String json = "{\n"
            + "  \"name\": \"Hammer\",\n"
            + "  \"description\": \"It's just a hammer :)\",\n"
            + "  \"toolCategory\": \"LIGHT\",\n"
            + "  \"price\": 10.99,\n"
            + "  \"toolState\": {\n"
            + "    \"stateType\": \"NEW\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(TOOL_ADD_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getWorkerAuthorizationToken());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(toolService.getToolById(1L).isEmpty());
    }

}
