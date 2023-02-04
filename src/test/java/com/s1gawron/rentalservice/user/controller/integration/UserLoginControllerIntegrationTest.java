package com.s1gawron.rentalservice.user.controller.integration;

import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    @Test
    void shouldLoginAndReturnValidTokenInHeader() throws Exception {
        final UserLoginRequest userLoginRequest = new UserLoginRequest(EMAIL, PASSWORD);

        final MvcResult result = performLoginAction(userLoginRequest);
        final AuthenticationResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(authResponse.token());
    }

    @Test
    void shouldReturnForbiddenStatus() throws Exception {
        final UserLoginRequest userLoginRequest = new UserLoginRequest("test@test.pl", "wrongPassword");

        final MvcResult result = performLoginAction(userLoginRequest);

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}
