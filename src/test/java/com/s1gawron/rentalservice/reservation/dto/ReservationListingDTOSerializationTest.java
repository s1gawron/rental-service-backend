package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1gawron.rentalservice.reservation.helper.ReservationCreatorHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ReservationListingDTOSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldSerialize() {
        final List<ReservationDetailsDTO> reservationDetailsList = ReservationCreatorHelper.I.createReservationDetailsListWithFixedDate();
        final ReservationListingDTO reservationListingDTO = ReservationListingDTO.create(reservationDetailsList);

        final String reservationListingDTOJsonResult = mapper.writeValueAsString(reservationListingDTO);
        final String expectedReservationListingDTOJsonResult = Files.readString(Path.of("src/test/resources/reservation-listing-dto.json"));

        final JsonNode expected = mapper.readTree(expectedReservationListingDTOJsonResult);
        final JsonNode result = mapper.readTree(reservationListingDTOJsonResult);

        Assertions.assertEquals(expected, result);
    }

}