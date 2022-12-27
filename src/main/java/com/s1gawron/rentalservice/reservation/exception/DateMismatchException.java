package com.s1gawron.rentalservice.reservation.exception;

public class DateMismatchException extends RuntimeException {

    private DateMismatchException(final String message) {
        super(message);
    }

    public static DateMismatchException createForDateFrom() {
        return new DateMismatchException("Date from cannot be before current date!");
    }

    public static DateMismatchException createForDateTo() {
        return new DateMismatchException("Due date cannot be before current date!");
    }
}
