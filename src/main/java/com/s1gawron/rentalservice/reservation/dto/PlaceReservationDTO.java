package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.tool.dto.ToolDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = PlaceReservationDTO.PlaceReservationDTOBuilder.class)
public class PlaceReservationDTO {

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private String additionalComment;

    private final List<ToolDTO> toolReservations;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PlaceReservationDTOBuilder {

    }

}
