package com.s1gawron.rentalservice.user.controller.webmvc;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import com.s1gawron.rentalservice.user.controller.UserController;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.*;
import com.s1gawron.rentalservice.user.model.UserRole;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@WithMockUser
class UserRegisterControllerTest extends AbstractUserControllerTest {

    private static final String USER_REGISTER_ENDPOINT = "/api/public/user/register";

    @Test
    void shouldRegisterUser() throws Exception {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserDTO userDTO = new UserDTO("John", "Kowalski", "test@test.pl", UserRole.CUSTOMER.name(), addressDTO);

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenReturn(userDTO);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);
        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(userDTO.email(), userDTOResult.email());
        assertEquals(userDTO.email(), userDTOResult.email());
        assertEquals(userDTO.customerAddress().country(), userDTOResult.customerAddress().country());
        assertEquals(userDTO.customerAddress().postCode(), userDTOResult.customerAddress().postCode());
    }

    @Test
    void shouldReturnForbiddenResponseWhenWorkerIsNotRegisteredByAdmin() throws Exception {
        final WorkerRegisteredByNonAdminUserException expectedException = WorkerRegisteredByNonAdminUserException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isForbidden())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnConflictResponseWhenUserEmailAlreadyExistsWhileRegisteringUser() throws Exception {
        final UserEmailExistsException expectedException = UserEmailExistsException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isConflict())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenAddressRegisterPropertiesAreEmptyWhileRegisteringUser() throws Exception {
        final AddressRegisterEmptyPropertiesException expectedException = AddressRegisterEmptyPropertiesException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenRegisterPropertiesAreEmptyWhileRegisteringUser() throws Exception {
        final UserRegisterEmptyPropertiesException expectedException = UserRegisterEmptyPropertiesException.createForPassword();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenEmailPatternDoesNotMatchWhileRegisteringUser() throws Exception {
        final UserEmailPatternViolationException expectedException = UserEmailPatternViolationException.create("test-test.pl");

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenPasswordIsTooWeakWhileRegisteringUser() throws Exception {
        final UserPasswordTooWeakException expectedException = UserPasswordTooWeakException.create();

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

    @Test
    void shouldReturnBadRequestResponseWhenPostCodePatternDoesNotMatchWhileRegisteringUser() throws Exception {
        final PostCodePatternViolationException expectedException = PostCodePatternViolationException.create("00000");

        Mockito.when(userServiceMock.validateAndRegisterUser(Mockito.any(UserRegisterDTO.class))).thenThrow(expectedException);

        final RequestBuilder request = MockMvcRequestBuilders.post(USER_REGISTER_ENDPOINT).with(csrf()).content(userRegisterJson)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(ERROR_RESPONSE_MESSAGE_PLACEHOLDER).value(expectedException.getMessage()));
    }

}