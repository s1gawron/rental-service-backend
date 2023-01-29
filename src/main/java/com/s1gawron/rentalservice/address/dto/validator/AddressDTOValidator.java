package com.s1gawron.rentalservice.address.dto.validator;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum AddressDTOValidator {

    I;

    private static final Logger log = LoggerFactory.getLogger(AddressDTOValidator.class);

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern POLISH_POST_CODE_PATTERN = Pattern.compile("[0-9]{2}-[0-9]{3}");

    public boolean validate(final AddressDTO addressDTO) {
        if (addressDTO.country() == null) {
            log.error("Country" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCountry();
        }

        if (addressDTO.city() == null) {
            log.error("City" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCity();
        }

        if (addressDTO.street() == null) {
            log.error("Street" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForStreet();
        }

        if (addressDTO.postCode() == null) {
            log.error("Post code" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForPostCode();
        }

        final Matcher postCodeMatcher = POLISH_POST_CODE_PATTERN.matcher(addressDTO.postCode());

        if (!postCodeMatcher.matches()) {
            log.error("Provided post code does not match pattern: XX-XXX!!!");
            throw PostCodePatternViolationException.create(addressDTO.postCode());
        }

        return true;
    }

}
