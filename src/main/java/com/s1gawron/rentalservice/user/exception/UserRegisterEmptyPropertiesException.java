package com.s1gawron.rentalservice.user.exception;

public class UserRegisterEmptyPropertiesException extends RuntimeException {

    private UserRegisterEmptyPropertiesException(final String message) {
        super(message);
    }

    public static UserRegisterEmptyPropertiesException createForFirstName() {
        return new UserRegisterEmptyPropertiesException("First name cannot be empty!");
    }

    public static UserRegisterEmptyPropertiesException createForLastName() {
        return new UserRegisterEmptyPropertiesException("Last name cannot be empty!");
    }

    public static UserRegisterEmptyPropertiesException createForUserType() {
        return new UserRegisterEmptyPropertiesException("User type cannot be empty!");
    }

    public static UserRegisterEmptyPropertiesException createForEmail() {
        return new UserRegisterEmptyPropertiesException("User email cannot be empty!");
    }

    public static UserRegisterEmptyPropertiesException createForPassword() {
        return new UserRegisterEmptyPropertiesException("User password cannot be empty!");
    }
}
