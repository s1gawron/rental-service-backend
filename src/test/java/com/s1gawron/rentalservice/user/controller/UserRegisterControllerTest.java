package com.s1gawron.rentalservice.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import com.s1gawron.rentalservice.jwt.JwtConfig;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.*;
import com.s1gawron.rentalservice.user.model.UserType;
import com.s1gawron.rentalservice.user.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@WithMockUser
class UserRegisterControllerTest {

    private static final AddressDTO ADDRESS_DTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

    private static final UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", UserType.CUSTOMER,
        ADDRESS_DTO);

    private static final String USER_REGISTER_ENDPOINT = "/api/user/register";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSourceMock;

    @MockBean
    private JwtConfig jwtConfigMock;

    @MockBean
    private UserService userServiceMock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String userRegisterJson;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        userRegisterJson = objectMapper.writeValueAsString(USER_REGISTER_DTO);
    }

    @Test
    @SneakyThrows
    void shouldRegisterUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserDTO userDTO = new UserDTO("John", "Kowalski", "test@test.pl", addressDTO);

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenReturn(userDTO);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(userDTO.getEmail(), userDTOResult.getEmail());
        assertEquals(userDTO.getEmail(), userDTOResult.getEmail());
        assertEquals(userDTO.getCustomerAddress().getCountry(), userDTOResult.getCustomerAddress().getCountry());
        assertEquals(userDTO.getCustomerAddress().getPostCode(), userDTOResult.getCustomerAddress().getPostCode());
    }

    @Test
    @SneakyThrows
    void shouldReturnConflictResponseWhenUserEmailAlreadyExistsWhileRegisteringUser() {
        final UserEmailExistsException userEmailExistsException = UserEmailExistsException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(userEmailExistsException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.CONFLICT, userEmailExistsException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenAddressRegisterPropertiesAreEmptyWhileRegisteringUser() {
        final AddressRegisterEmptyPropertiesException addressRegisterEmptyPropertiesException = AddressRegisterEmptyPropertiesException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(addressRegisterEmptyPropertiesException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, addressRegisterEmptyPropertiesException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenRegisterPropertiesAreEmptyWhileRegisteringUser() {
        final UserRegisterEmptyPropertiesException userRegisterEmptyPropertiesException = UserRegisterEmptyPropertiesException.createForPassword();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(userRegisterEmptyPropertiesException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, userRegisterEmptyPropertiesException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenEmailPatternDoesNotMatchWhileRegisteringUser() {
        final UserEmailPatternViolationException userEmailPatternViolationException = UserEmailPatternViolationException.create("test-test.pl");

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(userEmailPatternViolationException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, userEmailPatternViolationException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenPasswordIsTooWeakWhileRegisteringUser() {
        final UserPasswordTooWeakException userPasswordTooWeakException = UserPasswordTooWeakException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(userPasswordTooWeakException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, userPasswordTooWeakException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenPostCodePatternDoesNotMatchWhileRegisteringUser() {
        final PostCodePatternViolationException postCodePatternViolationException = PostCodePatternViolationException.create("00000");

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(postCodePatternViolationException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, postCodePatternViolationException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

    private void assertErrorResponse(final HttpStatus expectedStatus, final String expectedMessage, final String expectedUri,
        final ErrorResponse actualErrorResponse) {
        assertEquals(expectedStatus.value(), actualErrorResponse.getCode());
        assertEquals(expectedStatus.getReasonPhrase(), actualErrorResponse.getError());
        assertEquals(expectedMessage, actualErrorResponse.getMessage());
        assertEquals(expectedUri, actualErrorResponse.getURI());
    }

    @SneakyThrows
    private ErrorResponse toErrorResponse(final String responseMessage) {
        return objectMapper.readValue(responseMessage, ErrorResponse.class);
    }

}