package com.s1gawron.rentalservice.address.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AddressDTODeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String addressJson = Files.readString(Path.of("src/test/resources/address-dto.json"));
        final AddressDTO result = mapper.readValue(addressJson, AddressDTO.class);

        assertEquals("Poland", result.getCountry());
        assertEquals("Warsaw", result.getCity());
        assertEquals("Test", result.getStreet());
        assertEquals("01-000", result.getPostCode());
    }

    @Test
    void shouldValidate() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

        assertTrue(addressDTO.validate());
    }

    @Test
    void shouldThrowExceptionWhenCountryIsNull() {
        final AddressDTO addressDTO = new AddressDTO(null, "Warsaw", "Test", "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, addressDTO::validate, "Country cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", null, "Test", "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, addressDTO::validate, "City cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenStreetIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", null, "01-000");

        assertThrows(AddressRegisterEmptyPropertiesException.class, addressDTO::validate, "Street cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPostCodeIsNull() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", null);

        assertThrows(AddressRegisterEmptyPropertiesException.class, addressDTO::validate, "Post code cannot be empty!");
    }

    @Test
    void shouldThrowExceptionWhenPostCodeDoesNotMatchPattern() {
        final String postCode = "000";
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", postCode);

        assertThrows(PostCodePatternViolationException.class, addressDTO::validate, "Post code: " + postCode + ", does not match validation pattern!");
    }

}