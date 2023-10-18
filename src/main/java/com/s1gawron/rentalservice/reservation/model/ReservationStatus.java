package com.s1gawron.rentalservice.reservation.model;

public enum ReservationStatus {
    ACTIVE,
    CANCELED,
    COMPLETED;

    public boolean isCanceled() {
        return this == CANCELED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
