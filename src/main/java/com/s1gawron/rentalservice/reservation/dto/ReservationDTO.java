package com.s1gawron.rentalservice.reservation.dto;

import java.time.LocalDate;
import java.util.List;

public record ReservationDTO(LocalDate dateFrom, LocalDate dateTo, String additionalComment, List<Long> toolIds) {

}
