package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.security.JwtService;
import com.s1gawron.rentalservice.shared.exception.UserNotFoundException;
import com.s1gawron.rentalservice.user.dto.AuthenticationResponse;
import com.s1gawron.rentalservice.user.dto.UserLoginDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationServiceTest {

    private static final String CUSTOMER_EMAIL = "customer@test.pl";

    private static final String PASSWORD = "Start00!";

    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckB0ZXN0LnBsIiwiaWF0IjoxNjg3NTkyNDg4LCJleHAiOjE2ODc1OTYwODh9.mnWvLAAflDV6PthsNXwgco-DP9D7pMSvV78y5gRraUA";

    private UserDAO userDAOMock;

    private JwtService jwtServiceMock;

    private UserAuthenticationService userAuthenticationService;

    @BeforeEach
    void setUp() {
        final AuthenticationManager authenticationManagerMock = Mockito.mock(AuthenticationManager.class);
        userDAOMock = Mockito.mock(UserDAO.class);
        jwtServiceMock = Mockito.mock(JwtService.class);
        userAuthenticationService = new UserAuthenticationService(userDAOMock, authenticationManagerMock, jwtServiceMock);
    }

    @Test
    void shouldLoginUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(CUSTOMER_EMAIL, PASSWORD, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(customerRegisterDTO, PASSWORD);
        final UserLoginDTO userLoginDTO = new UserLoginDTO(CUSTOMER_EMAIL, PASSWORD);

        Mockito.when(userDAOMock.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(jwtServiceMock.generateToken(Map.of(), user)).thenReturn(JWT_TOKEN);

        final AuthenticationResponse result = userAuthenticationService.loginUser(userLoginDTO);

        assertNotNull(result);
        assertTrue(isStringNotEmpty(result.token()));
        assertEquals(JWT_TOKEN, result.token());
    }

    @Test
    void shouldNotLoginUser() {
        final UserLoginDTO userLoginDTO = new UserLoginDTO(CUSTOMER_EMAIL, PASSWORD);

        assertThrows(UserNotFoundException.class, () -> userAuthenticationService.loginUser(userLoginDTO), "User#customer@test.pl could not be found!");
    }

    private boolean isStringNotEmpty(final String s) {
        final String trimmedString = s != null ? s.trim() : "";
        return trimmedString.length() > 0;
    }

}