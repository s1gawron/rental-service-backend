package com.s1gawron.rentalservice.address.exception;

public class PostCodePatternViolationException extends RuntimeException {

    public PostCodePatternViolationException(final String message) {
        super(message);
    }

    public static PostCodePatternViolationException create(final String postCode) {
        return new PostCodePatternViolationException(
            "Post code: " + postCode + ", does not match validation pattern!");
    }
}
