package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailPatternViolationException;
import com.s1gawron.rentalservice.user.exception.UserPasswordTooWeakException;
import com.s1gawron.rentalservice.user.exception.UserRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.user.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Log4j2
@Builder
@Getter
@JsonDeserialize(builder = UserRegisterDTO.UserRegisterDTOBuilder.class)
public class UserRegisterDTO {

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$*])(?!.*\\s).{8,32}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private final String email;

    private final String password;

    private String firstName;

    private String lastName;

    private UserType userType;

    private AddressDTO address;

    public boolean validate() {
        if (this.email == null) {
            log.error("Email" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForEmail();
        }

        if (this.password == null) {
            log.error("Password" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForPassword();
        }

        if (this.firstName == null) {
            log.error("First name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForFirstName();
        }

        if (this.lastName == null) {
            log.error("Last name" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForLastName();
        }

        if (this.userType == null) {
            log.error("User type" + MESSAGE);
            throw UserRegisterEmptyPropertiesException.createForUserType();
        }

        if (this.userType == UserType.CUSTOMER) {
            this.address.validate();
        }

        final Matcher emailMatcher = EMAIL_PATTERN.matcher(this.email);

        if (!emailMatcher.matches()) {
            log.error("Provided email: {}, does not match pattern in registration process", this.email);
            throw UserEmailPatternViolationException.create(this.email);
        }

        final Matcher passwordMatcher = PASSWORD_PATTERN.matcher(this.password);

        if (!passwordMatcher.matches()) {
            log.error("Password does not meet security policy in registration process");
            throw UserPasswordTooWeakException.create();
        }

        return true;
    }

}
