package com.s1gawron.rentalservice.user.controller.webmvc;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import com.s1gawron.rentalservice.user.controller.UserController;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.*;
import com.s1gawron.rentalservice.user.model.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@WithMockUser
class UserRegisterControllerTest extends AbstractUserControllerTest {

    private static final String USER_REGISTER_ENDPOINT = "/api/public/user/register";

    @Test
    @SneakyThrows
    void shouldRegisterUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserDTO userDTO = new UserDTO("John", "Kowalski", "test@test.pl", UserRole.CUSTOMER.getName(), addressDTO);

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

    @Test
    @SneakyThrows
    void shouldReturnBadRequestResponseWhenUserRoleDoesNotExistWhileRegisteringUser() {
        final UserRoleDoesNotExistException userRegisterEmptyPropertiesException = UserRoleDoesNotExistException.create("UNKNOWN");

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(userRegisterEmptyPropertiesException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).content(userRegisterJson).contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertErrorResponse(HttpStatus.BAD_REQUEST, userRegisterEmptyPropertiesException.getMessage(), USER_REGISTER_ENDPOINT,
            toErrorResponse(result.getResponse().getContentAsString()));
    }

}