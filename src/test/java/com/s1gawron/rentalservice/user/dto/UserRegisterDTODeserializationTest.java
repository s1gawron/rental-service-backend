package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.user.model.UserType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterDTODeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String userRegisterJson = Files.readString(Path.of("src/test/resources/user-register.json"));
        final UserRegisterDTO result = mapper.readValue(userRegisterJson, UserRegisterDTO.class);

        assertEquals("test@test.pl", result.getEmail());
        assertEquals("Start00!", result.getPassword());
        assertEquals("John", result.getFirstName());
        assertEquals("Kowalski", result.getLastName());
        assertEquals(UserType.CUSTOMER, result.getUserType());
        assertEquals("Poland", result.getAddress().getCountry());
        assertEquals("01-000", result.getAddress().getPostCode());
    }

    @Test
    void shouldValidate() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", UserType.CUSTOMER, addressDTO);

        assertTrue(userRegisterDTO.validate());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(null, "Start00!", "John", "Kowalski", UserType.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, userRegisterDTO::validate, "User email cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", null, "John", "Kowalski", UserType.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, userRegisterDTO::validate, "User password cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", "Start00!", null, "Kowalski", UserType.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, userRegisterDTO::validate, "User first name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", null, UserType.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, userRegisterDTO::validate, "User last name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenUserTypeIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", "Start00!", "John", "Kowalski", null, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, userRegisterDTO::validate, "User type cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotMatchPattern() {
        final String email = "test-test.pl";
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(email, "Start00!", "John", "Kowalski", UserType.CUSTOMER, addressDTO);

        assertThrows(UserEmailPatternViolationException.class, userRegisterDTO::validate,
            "Email: " + email + ", does not match validation pattern. If this is proper email please contact me for a fix.");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooWeak() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO("test@test.pl", "password", "John", "Kowalski", UserType.CUSTOMER, addressDTO);

        assertThrows(UserPasswordTooWeakException.class, userRegisterDTO::validate,
            "Provided password is too weak! The password must be minimum 8 characters long and contain upper and lower case letters, a number and one of the characters !, @, #, $, *");
    }

}