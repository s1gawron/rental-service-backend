package com.s1gawron.rentalservice.user.controller.integration;

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
        final String token = result.getResponse().getHeader("Authorization");

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer"));
    }

    @Test
    void shouldReturnUnauthorizedStatus() throws Exception {
        final UserLoginDTO userLoginDTO = new UserLoginDTO("testUser", "wrongPassword");

        final MvcResult result = performLoginAction(userLoginDTO);
        final String token = result.getResponse().getHeader("Authorization");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());

        assertNull(token);
    }

}
