package com.s1gawron.rentalservice.user.controller.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    private static final String USER_REGISTER_ENDPOINT = "/api/public/user/v1/register";

    @Test
    void shouldRegisterUser() throws Exception {
        final String json = """
            {
              "email": "new@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "CUSTOMER",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new@test.pl").isPresent());
    }

    @Test
    void shouldRegisterWorkerByAdminUser() throws Exception {
        final String json = """
            {
              "email": "new@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "WORKER",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAuthorizationTokenForAdmin());

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new@test.pl").isPresent());
    }

    @Test
    void shouldNotRegisterWorkerWhenNotInvokedByAdmin() throws Exception {
        final String json = """
            {
              "email": "new@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "WORKER",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new@test.pl").isEmpty());
    }

    @Test
    void shouldNotRegisterUserAndReturnBadRequestWhenUserRoleDoesNotExist() throws Exception {
        final String json = """
            {
              "email": "no-role@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "UNKNOWN",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("no-role@test.pl").isEmpty());
    }

    @Test
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailIsEmpty() throws Exception {
        final String json = """
            {
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "UNKNOWN",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldNotRegisterUserAndReturnBadRequestWhenEmailPatternIsWrong() throws Exception {
        final String json = """
            {
              "email": "new-test-pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "UNKNOWN",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("new-test-pl").isEmpty());
    }

    @Test
    void shouldNotRegisterUserAndReturnBadRequestWhenPasswordIsWeak() throws Exception {
        final String json = """
            {
              "email": "weak-password@test.pl",
              "password": "password",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "CUSTOMER",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("weak-password@test.pl").isEmpty());
    }

    @Test
    void shouldNotRegisterUserAndReturnBadRequestWhenPostCodePatternIsWrong() throws Exception {
        final String json = """
            {
              "email": "wrong-post-code@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "UNKNOWN",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "0100"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(userService.getUserByEmail("wrong-post-code@test.pl").isEmpty());
    }

    @Test
    void shouldReturnConflictResponseWhenUserEmailAlreadyExistsWhileRegisteringUser() throws Exception {
        final String json = """
            {
              "email": "new@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "CUSTOMER",
              "address": {
                "country": "Poland",
                "city": "Warsaw",
                "street": "Test",
                "postCode": "01-000"
              }
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andReturn();
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.CONFLICT.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldReturnBadRequestResponseWhenAddressRegisterPropertiesAreEmptyWhileRegisteringUser() throws Exception {
        final String json = """
            {
              "email": "new@test.pl",
              "password": "Start00!",
              "firstName": "John",
              "lastName": "Kowalski",
              "userRole": "CUSTOMER"
            }""";
        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(json).contentType(MediaType.APPLICATION_JSON);

        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertFalse(userService.getUserByEmail("new@test.pl").isPresent());
    }

}
