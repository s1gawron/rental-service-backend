package com.s1gawron.rentalservice.user.dto.validator;

import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum UserDTOValidator {

    I;

    private static final Logger log = LoggerFactory.getLogger(UserDTOValidator.class);

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$*])(?!.*\\s).{8,32}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    public boolean validate(final UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest.email() == null) {
            log.error("Email" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForEmail();
        }

        if (userRegisterRequest.password() == null) {
            log.error("Password" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForPassword();
        }

        if (userRegisterRequest.firstName() == null) {
            log.error("First name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForFirstName();
        }

        if (userRegisterRequest.lastName() == null) {
            log.error("Last name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForLastName();
        }

        if (userRegisterRequest.userRole() == null) {
            log.error("User type" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForUserRole();
        }

        final Matcher emailMatcher = EMAIL_PATTERN.matcher(userRegisterRequest.email());

        if (!emailMatcher.matches()) {
            log.error("Provided email: {}, does not match pattern in registration process", userRegisterRequest.email());
            throw UserEmailPatternViolationException.create(userRegisterRequest.email());
        }

        final Matcher passwordMatcher = PASSWORD_PATTERN.matcher(userRegisterRequest.password());

        if (!passwordMatcher.matches()) {
            log.error("Password does not meet security policy in registration process");
            throw UserPasswordTooWeakException.create();
        }

        return true;
    }
}
