package com.s1gawron.rentalservice.address.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}