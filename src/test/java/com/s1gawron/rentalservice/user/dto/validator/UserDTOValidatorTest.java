package com.s1gawron.rentalservice.user.dto.validator;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDTOValidatorTest {

    @Test
    void shouldValidate() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertTrue(UserDTOValidator.I.validate(userRegisterRequest));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(null, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, () -> UserDTOValidator.I.validate(userRegisterRequest), "User email cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", null, "John", "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, () -> UserDTOValidator.I.validate(userRegisterRequest), "User password cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", "Start00!", null, "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, () -> UserDTOValidator.I.validate(userRegisterRequest), "User first name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", "Start00!", "John", null, UserRole.CUSTOMER, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, () -> UserDTOValidator.I.validate(userRegisterRequest), "User last name cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", "Start00!", "John", "Kowalski", null, addressDTO);

        assertThrows(UserRegisterEmptyPropertiesException.class, () -> UserDTOValidator.I.validate(userRegisterRequest), "User type cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotMatchPattern() {
        final String email = "test-test.pl";
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(email, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertThrows(UserEmailPatternViolationException.class, () -> UserDTOValidator.I.validate(userRegisterRequest),
                "Email: " + email + ", does not match validation pattern. If this is proper email please contact me for a fix.");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooWeak() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test@test.pl", "password", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);

        assertThrows(UserPasswordTooWeakException.class, () -> UserDTOValidator.I.validate(userRegisterRequest),
                "Provided password is too weak! The password must be minimum 8 characters long and contain upper and lower case letters, a number and one of the characters !, @, #, $, *");
    }

}