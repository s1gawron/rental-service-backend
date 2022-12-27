package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationDTODeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldDeserialize() {
        final String reservationJson = Files.readString(Path.of("src/test/resources/reservation-dto.json"));
        final ReservationDTO result = mapper.readValue(reservationJson, ReservationDTO.class);

        assertEquals(LocalDate.parse("2022-12-04"), result.getDateFrom());
        assertEquals(LocalDate.parse("2022-12-16"), result.getDateTo());
        assertEquals("Hammer, loader and a crane", result.getAdditionalComment());
        assertEquals(List.of(1L, 2L, 3L), result.getToolIds());
    }

}