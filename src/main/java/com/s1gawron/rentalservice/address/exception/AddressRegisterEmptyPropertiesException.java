package com.s1gawron.rentalservice.address.exception;

public class AddressRegisterEmptyPropertiesException extends RuntimeException {

    private AddressRegisterEmptyPropertiesException(final String message) {
        super(message);
    }

    public static AddressRegisterEmptyPropertiesException create() {
        return new AddressRegisterEmptyPropertiesException("Address cannot be empty!");
    }

    public static AddressRegisterEmptyPropertiesException createForCountry() {
        return new AddressRegisterEmptyPropertiesException("Country cannot be empty!");
    }

    public static AddressRegisterEmptyPropertiesException createForCity() {
        return new AddressRegisterEmptyPropertiesException("City cannot be empty!");
    }

    public static AddressRegisterEmptyPropertiesException createForStreet() {
        return new AddressRegisterEmptyPropertiesException("Street cannot be empty!");
    }

    public static AddressRegisterEmptyPropertiesException createForPostCode() {
        return new AddressRegisterEmptyPropertiesException("Post code cannot be empty!");
    }
}
