package com.s1gawron.rentalservice.user.exception;

public class UserRoleDoesNotExistException extends RuntimeException {

    private UserRoleDoesNotExistException(final String message) {
        super(message);
    }

    public static UserRoleDoesNotExistException create(final String role) {
        return new UserRoleDoesNotExistException("Role#" + role + " does not exist!");
    }
}
