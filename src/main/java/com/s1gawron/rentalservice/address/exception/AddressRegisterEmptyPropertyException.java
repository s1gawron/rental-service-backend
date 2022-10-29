package com.s1gawron.rentalservice.address.exception;

public class AddressRegisterEmptyPropertyException extends RuntimeException {

    private AddressRegisterEmptyPropertyException(final String message) {
        super(message);
    }

    public static AddressRegisterEmptyPropertyException createForCountry() {
        return new AddressRegisterEmptyPropertyException("Country cannot be empty!");
    }

    public static AddressRegisterEmptyPropertyException createForCity() {
        return new AddressRegisterEmptyPropertyException("City cannot be empty!");
    }

    public static AddressRegisterEmptyPropertyException createForStreet() {
        return new AddressRegisterEmptyPropertyException("Street cannot be empty!");
    }

    public static AddressRegisterEmptyPropertyException createForPostCode() {
        return new AddressRegisterEmptyPropertyException("Post code cannot be empty!");
    }
}
