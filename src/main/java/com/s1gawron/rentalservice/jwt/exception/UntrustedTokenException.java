package com.s1gawron.rentalservice.jwt.exception;

public class UntrustedTokenException extends IllegalStateException {

    private UntrustedTokenException(String s) {
        super(s);
    }

    public static UntrustedTokenException create() {
        return new UntrustedTokenException("Token cannot be trusted!");
    }
}
