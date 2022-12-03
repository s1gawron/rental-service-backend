package com.s1gawron.rentalservice.shared;

public class UserNotFoundException extends RuntimeException {

    private UserNotFoundException(final String message) {
        super(message);
    }

    public static UserNotFoundException create(final String email) {
        return new UserNotFoundException("User: " + email + " could not be found!");
    }
}
