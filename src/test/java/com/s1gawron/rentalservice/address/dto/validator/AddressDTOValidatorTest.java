package com.s1gawron.rentalservice.address.dto.validator;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressDTOValidatorTest {

    @Test
    void shouldValidate() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

        assertTrue(AddressDTOValidator.I.validate(addressDTO));
    }

    @Test
    void shouldThrowExceptionWhenCountryIsNull() {
        final AddressDTO addressDTO = new AddressDTO(null, "Warsaw", "Test", "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, () -> AddressDTOValidator.I.validate(addressDTO), "Country cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", null, "Test", "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, () -> AddressDTOValidator.I.validate(addressDTO), "City cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenStreetIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", null, "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, () -> AddressDTOValidator.I.validate(addressDTO), "Street cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPostCodeIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", null);

        assertThrows(AddressRegisterEmptyPropertiesException.class, () -> AddressDTOValidator.I.validate(addressDTO), "Post code cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPostCodeDoesNotMatchPattern() {
        final String postCode = "000";
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", postCode);

        assertThrows(PostCodePatternViolationException.class, () -> AddressDTOValidator.I.validate(addressDTO),
            "Post code: " + postCode + ", does not match validation pattern!");
    }

}