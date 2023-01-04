package com.s1gawron.rentalservice.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = ReservationDetailsDTO.ReservationDetailsDTOBuilder.class)
public class ReservationDetailsDTO {

    private final Long reservationId;

    private final boolean expired;

    private final boolean canceled;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate dateFrom;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate dateTo;

    private final BigDecimal reservationFinalPrice;

    private final String additionalComment;

    private final List<ToolDetailsDTO> tools;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ReservationDetailsDTOBuilder {

    }

}
