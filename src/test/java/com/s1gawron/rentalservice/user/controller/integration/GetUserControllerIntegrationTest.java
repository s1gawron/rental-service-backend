package com.s1gawron.rentalservice.user.controller.integration;

import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetUserControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    private static final String USER_DETAILS_ENDPOINT = "/api/user/details";

    @Test
    @SneakyThrows
    void shouldGetUserDetails() {
        final UserDTO expectedUserDto = userService.getUserByEmail(EMAIL)
            .orElseThrow(() -> new IllegalStateException("Expected user dto cannot be null!"))
            .toUserDTO();
        final UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);
        final MvcResult loginResult = performLoginAction(userLoginDTO);
        final String token = loginResult.getResponse().getHeader("Authorization");
        final RequestBuilder request = MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT).header("Authorization", token);

        final MvcResult result = mockMvc.perform(request).andReturn();
        final String jsonResult = result.getResponse().getContentAsString();
        final UserDTO userDTOResult = objectMapper.readValue(jsonResult, UserDTO.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(userDTOResult);
        assertEquals(expectedUserDto.getEmail(), userDTOResult.getEmail());
        assertEquals(expectedUserDto.getFirstName(), userDTOResult.getFirstName());
        assertEquals(expectedUserDto.getLastName(), userDTOResult.getLastName());
        assertEquals(expectedUserDto.getUserRole(), userDTOResult.getUserRole());
        assertEquals(expectedUserDto.getCustomerAddress().getCountry(), userDTOResult.getCustomerAddress().getCountry());
        assertEquals(expectedUserDto.getCustomerAddress().getPostCode(), userDTOResult.getCustomerAddress().getPostCode());
    }

    @Test
    @SneakyThrows
    void shouldNotGetUserDetailsAndReturnForbiddenWhenUserIsNotLoggedIn() {
        final RequestBuilder request = MockMvcRequestBuilders.get(USER_DETAILS_ENDPOINT);
        final MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}
