package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.shared.ObjectMapperCreator;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRegisterRequestDeserializationTest {

    private final ObjectMapper mapper = ObjectMapperCreator.I.getMapper();

    @Test
    void shouldDeserialize() throws IOException {
        final String userRegisterJson = Files.readString(Path.of("src/test/resources/user-register.json"));
        final UserRegisterRequest result = mapper.readValue(userRegisterJson, UserRegisterRequest.class);

        assertEquals("test@test.pl", result.email());
        assertEquals("Start00!", result.password());
        assertEquals("John", result.firstName());
        assertEquals("Kowalski", result.lastName());
        assertEquals(UserRole.CUSTOMER.name(), result.userRole());
        assertEquals("Poland", result.address().country());
        assertEquals("01-000", result.address().postCode());
    }

}