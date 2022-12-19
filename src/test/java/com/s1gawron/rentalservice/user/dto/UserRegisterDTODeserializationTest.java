package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.user.model.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(UserRole.CUSTOMER.getName(), result.getUserRole());
        assertEquals("Poland", result.getAddress().getCountry());
        assertEquals("01-000", result.getAddress().getPostCode());
    }

}