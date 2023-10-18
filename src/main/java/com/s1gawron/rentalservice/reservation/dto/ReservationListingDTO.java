package com.s1gawron.rentalservice.reservation.dto;

import java.util.List;

public record ReservationListingDTO(int numberOfPages, int totalNumberOfReservations, List<ReservationDetailsDTO> reservations) {

}
