package com.s1gawron.rentalservice.security;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String JWT_SECRET_KEY = "11111111111111111111111111111111";

    private static final String CUSTOMER_EMAIL = "customer@test.pl";

    private static final String DIFFERENT_CUSTOMER_EMAIL = "customer2@test.pl";

    private static final String PASSWORD = "Start00!";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        jwtService = new JwtService(JWT_SECRET_KEY, clock);
    }

    @Test
    void shouldGenerateToken() {
        final String result = jwtService.generateToken(Map.of(), createUser(CUSTOMER_EMAIL, "John", "Kowalski"));
        final String[] splitResult = result.split("\\.");

        assertTrue(isStringNotEmpty(result));
        assertEquals(3, splitResult.length);
        assertTrue(isStringNotEmpty(splitResult[0]));
        assertTrue(isStringNotEmpty(splitResult[1]));
        assertTrue(isStringNotEmpty(splitResult[2]));
    }

    @Test
    void shouldValidateToken() {
        final User user = createUser(CUSTOMER_EMAIL, "John", "Kowalski");
        final String token = jwtService.generateToken(Map.of(), user);

        final boolean result = jwtService.isTokenValid(token, user);

        assertTrue(result);
    }

    @Test
    void shouldNotValidateTokenWhenUserDetailsDoNotMatch() {
        final User user = createUser(CUSTOMER_EMAIL, "John", "Kowalski");
        final User differentUser = createUser(DIFFERENT_CUSTOMER_EMAIL, "Tony", "Hawk");
        final String token = jwtService.generateToken(Map.of(), user);

        final boolean result = jwtService.isTokenValid(token, differentUser);

        assertFalse(result);
    }

    @Test
    void shouldNotValidateTokenWhenTokenIsExpired() {
        final User user = createUser(CUSTOMER_EMAIL, "John", "Kowalski");
        final String token = generateExpiredToken(user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        final User user = createUser(CUSTOMER_EMAIL, "John", "Kowalski");
        final String token = jwtService.generateToken(Map.of(), user);

        final String result = jwtService.extractUsername(token);

        assertEquals(user.getUsername(), result);
    }

    private static String generateExpiredToken(final User user) {
        final Duration oneDayDuration = Duration.ofDays(1);
        final Clock oneDayExpiredClock = Clock.fixed(Instant.now().minus(oneDayDuration), ZoneId.systemDefault());
        final JwtService expiredJwtService = new JwtService(JWT_SECRET_KEY, oneDayExpiredClock);

        return expiredJwtService.generateToken(Map.of(), user);
    }

    private static User createUser(final String email, final String firstName, final String lastName) {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO customerRegisterDTO = new UserRegisterDTO(email, PASSWORD, firstName, lastName, UserRole.CUSTOMER, addressDTO);
        return User.createUser(customerRegisterDTO, PASSWORD);
    }

    private boolean isStringNotEmpty(final String s) {
        final String trimmedString = s != null ? s.trim() : "";
        return trimmedString.length() > 0;
    }

}