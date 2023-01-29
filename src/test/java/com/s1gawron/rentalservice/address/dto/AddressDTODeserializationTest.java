package com.s1gawron.rentalservice.address.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressDTODeserializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldDeserialize() throws IOException {
        final String addressJson = Files.readString(Path.of("src/test/resources/address-dto.json"));
        final AddressDTO result = mapper.readValue(addressJson, AddressDTO.class);

        assertEquals("Poland", result.country());
        assertEquals("Warsaw", result.city());
        assertEquals("Test", result.street());
        assertEquals("01-000", result.postCode());
    }

}