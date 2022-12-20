package com.s1gawron.rentalservice.user.controller.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRegisterControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    private static final String USER_REGISTER_ENDPOINT = "/api/public/user/register";

    @Test
    @SneakyThrows
    void shouldRegisterUser() {
        final String json = "{\n"
            + "  \"email\": \"new@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"CUSTOMER\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new@test.pl").isPresent());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenUserRoleDoesNotExist() {
        final String json = "{\n"
            + "  \"email\": \"no-role@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("no-role@test.pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailIsEmpty() {
        final String json = "{\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailPatternIsWrong() {
        final String json = "{\n"
            + "  \"email\": \"new-test-pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new-test-pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenPasswordIsWeak() {
        final String json = "{\n"
            + "  \"email\": \"weak-password@test.pl\",\n"
            + "  \"password\": \"password\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"CUSTOMER\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"01-000\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("weak-password@test.pl").isEmpty());
    }

    @Test
    @SneakyThrows
    void shouldNotRegisterUserAndReturnBadRequestWhenPostCodePatternIsWrong() {
        final String json = "{\n"
            + "  \"email\": \"wrong-post-code@test.pl\",\n"
            + "  \"password\": \"Start00!\",\n"
            + "  \"firstName\": \"John\",\n"
            + "  \"lastName\": \"Kowalski\",\n"
            + "  \"userRole\": \"UNKNOWN\",\n"
            + "  \"address\": {\n"
            + "    \"country\": \"Poland\",\n"
            + "    \"city\": \"Warsaw\",\n"
            + "    \"street\": \"Test\",\n"
            + "    \"postCode\": \"0100\"\n"
            + "  }\n"
            + "}";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("wrong-post-code@test.pl").isEmpty());
    }

}
