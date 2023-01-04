package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.tool.helper.ToolCreatorHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationDetailsDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final ReservationDetailsDTO reservationDetailsDTO = new ReservationDetailsDTO(1L, true, false, LocalDate.parse("2022-12-04"),
            LocalDate.parse("2022-12-16"), BigDecimal.valueOf(10.99), "Hammer, loader and a crane", ToolCreatorHelper.I.createToolDTOList());

        final String toolDTOJsonResult = mapper.writeValueAsString(reservationDetailsDTO);
        final String expectedReservationDetailsDTOJsonResult = Files.readString(Path.of("src/test/resources/reservation-details-dto.json"));

        final JsonNode expected = mapper.readTree(expectedReservationDetailsDTOJsonResult);
        final JsonNode result = mapper.readTree(toolDTOJsonResult);

        assertEquals(expected, result);
    }

}