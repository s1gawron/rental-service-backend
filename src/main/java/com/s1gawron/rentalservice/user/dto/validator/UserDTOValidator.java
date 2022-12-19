package com.s1gawron.rentalservice.user.dto.validator;

import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.user.model.UserRole;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public enum UserDTOValidator {

    I;

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$*])(?!.*\\s).{8,32}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    public boolean validate(final UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO.getEmail() == null) {
            log.error("Email" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForEmail();
        }

        if (userRegisterDTO.getPassword() == null) {
            log.error("Password" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForPassword();
        }

        if (userRegisterDTO.getFirstName() == null) {
            log.error("First name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForFirstName();
        }

        if (userRegisterDTO.getLastName() == null) {
            log.error("Last name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForLastName();
        }

        if (userRegisterDTO.getUserRole() == null) {
            log.error("User type" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForUserRole();
        }

        UserRole.findByValue(userRegisterDTO.getUserRole());

        final Matcher emailMatcher = EMAIL_PATTERN.matcher(userRegisterDTO.getEmail());

        if (!emailMatcher.matches()) {
            log.error("Provided email: {}, does not match pattern in registration process", userRegisterDTO.getEmail());
            throw UserEmailPatternViolationException.create(userRegisterDTO.getEmail());
        }

        final Matcher passwordMatcher = PASSWORD_PATTERN.matcher(userRegisterDTO.getPassword());

        if (!passwordMatcher.matches()) {
            log.error("Password does not meet security policy in registration process");
            throw UserPasswordTooWeakException.create();
        }

        return true;
    }
}
