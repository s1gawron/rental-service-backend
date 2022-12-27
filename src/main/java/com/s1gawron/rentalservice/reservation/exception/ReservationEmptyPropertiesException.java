package com.s1gawron.rentalservice.reservation.exception;

public class ReservationEmptyPropertiesException extends RuntimeException {

    private ReservationEmptyPropertiesException(final String message) {
        super(message);
    }

    public static ReservationEmptyPropertiesException createForDateFrom() {
        return new ReservationEmptyPropertiesException("Date from cannot be null!");
    }

    public static ReservationEmptyPropertiesException createForDateTo() {
        return new ReservationEmptyPropertiesException("Due date cannot be null!");
    }

    public static ReservationEmptyPropertiesException createForToolsList() {
        return new ReservationEmptyPropertiesException("Tools must be added to reservation!");
    }

}
