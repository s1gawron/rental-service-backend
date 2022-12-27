package com.s1gawron.rentalservice.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {

    private ReservationNotFoundException(final String message) {
        super(message);
    }

    public static ReservationNotFoundException create(final Long reservationId) {
        return new ReservationNotFoundException("Reservation#" + reservationId + " was not found!");
    }
}
