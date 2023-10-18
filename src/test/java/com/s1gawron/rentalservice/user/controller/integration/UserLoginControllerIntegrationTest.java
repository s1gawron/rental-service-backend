package com.s1gawron.rentalservice.user.controller.integration;

import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginControllerIntegrationTest extends AbstractUserControllerIntegrationTest {

    @Test
    void shouldLoginAndReturnValidTokenInHeader() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);

        final MvcResult result = performLoginAction(userLoginDTO);
        final AuthenticationResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(authResponse.token());
    }

    @Test
    void shouldReturnForbiddenStatus() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO("test@test.pl", "wrongPassword");

        final MvcResult result = performLoginAction(userLoginDTO);

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    }

}
