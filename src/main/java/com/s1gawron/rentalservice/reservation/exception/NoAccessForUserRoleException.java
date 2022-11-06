package com.s1gawron.rentalservice.reservation.exception;

public class NoAccessForUserRoleException extends RuntimeException {

    private NoAccessForUserRoleException(final String message) {
        super(message);
    }

    public static NoAccessForUserRoleException create(final String element) {
        return new NoAccessForUserRoleException("Current user role is not allowed to use: " + element + " module!");
    }

}
