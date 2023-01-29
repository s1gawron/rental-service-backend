package com.s1gawron.rentalservice.user.dto.validator;

import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.user.model.UserRole;
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

    public boolean validate(final UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO.email() == null) {
            log.error("Email" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForEmail();
        }

        if (userRegisterDTO.password() == null) {
            log.error("Password" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForPassword();
        }

        if (userRegisterDTO.firstName() == null) {
            log.error("First name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForFirstName();
        }

        if (userRegisterDTO.lastName() == null) {
            log.error("Last name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForLastName();
        }

        if (userRegisterDTO.userRole() == null) {
            log.error("User type" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForUserRole();
        }

        UserRole.findByValue(userRegisterDTO.userRole());

        final Matcher emailMatcher = EMAIL_PATTERN.matcher(userRegisterDTO.email());

        if (!emailMatcher.matches()) {
            log.error("Provided email: {}, does not match pattern in registration process", userRegisterDTO.email());
            throw UserEmailPatternViolationException.create(userRegisterDTO.email());
        }

        final Matcher passwordMatcher = PASSWORD_PATTERN.matcher(userRegisterDTO.password());

        if (!passwordMatcher.matches()) {
            log.error("Password does not meet security policy in registration process");
            throw UserPasswordTooWeakException.create();
        }

        return true;
    }
}
