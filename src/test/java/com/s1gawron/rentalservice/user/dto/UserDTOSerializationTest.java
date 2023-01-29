package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class UserDTOSerializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldSerialize() throws IOException {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserDTO userDTO = new UserDTO("John", "Kowalski", "test@test.pl", "CUSTOMER", addressDTO);

        final String userDTOJsonResult = mapper.writeValueAsString(userDTO);
        final String expectedUserDTOJsonResult = Files.readString(Path.of("src/test/resources/user-dto.json"));

        final JsonNode expected = mapper.readTree(expectedUserDTOJsonResult);
        final JsonNode result = mapper.readTree(userDTOJsonResult);

        Assertions.assertEquals(expected, result);
    }

}