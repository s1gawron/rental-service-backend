package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ReservationDTO.ReservationDTOBuilder.class)
public class ReservationDTO {

    private final LocalDate dateFrom;

    private final LocalDate dateTo;

    private final BigDecimal reservationFinalPrice;

    private final String additionalComment;

    private final List<ToolDTO> tools;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReservationDTOBuilder {

    }

    public static ReservationDTO from(final Reservation reservation, final List<ToolDTO> tools) {
        return new ReservationDTO(reservation.getDateFrom(), reservation.getDateTo(), reservation.getReservationFinalPrice(),
            reservation.getAdditionalComment(), tools);
    }

}
