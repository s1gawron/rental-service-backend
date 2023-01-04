package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ReservationListingDTO.ReservationListingDTOBuilder.class)
public class ReservationListingDTO {

    private final int count;

    private final List<ReservationDetailsDTO> reservations;

    public static ReservationListingDTO create(final List<ReservationDetailsDTO> reservationDetailsDTOS) {
        return new ReservationListingDTO(reservationDetailsDTOS.size(), reservationDetailsDTOS);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReservationListingDTOBuilder {

    }

}
