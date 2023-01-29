package com.s1gawron.rentalservice.reservation.dto;

import com.s1gawron.rentalservice.tool.dto.ToolDetailsDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservationDetailsDTO(Long reservationId, boolean expired, boolean canceled, LocalDate dateFrom, LocalDate dateTo,
                                    BigDecimal reservationFinalPrice, String additionalComment, List<ToolDetailsDTO> tools) {

}
