package com.s1gawron.rentalservice.user.controller.integration;

import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserManagementControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    private static final String USER_DETAILS_ENDPOINT = "/api/management/user/v1/details";

    @Test
    void shouldGetUserDetails() throws Exception {
        final UserDTO expectedUserDto = userService.getUserByEmail(EMAIL)
            .orElseThrow(() -> new IllegalStateException("Expected user dto cannot be null!"))
            .toUserDTO();
        final UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);
        final MvcResult loginResult = performLoginAction(userLoginDTO);
        final AuthenticationResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthenticationResponse.class);
        final String token = "Bearer " + authResponse.token();
        final RequestBuilder request = MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT).header("Authorization", token);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(expectedUserDto.email(), userDTOResult.email());
        assertEquals(expectedUserDto.firstName(), userDTOResult.firstName());
        assertEquals(expectedUserDto.lastName(), userDTOResult.lastName());
        assertEquals(expectedUserDto.userRole(), userDTOResult.userRole());
        assertEquals(expectedUserDto.customerAddress().country(), userDTOResult.customerAddress().country());
        assertEquals(expectedUserDto.customerAddress().postCode(), userDTOResult.customerAddress().postCode());
    }

    @Test
    void shouldNotGetUserDetailsAndReturnForbiddenWhenUserIsNotLoggedIn() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}
