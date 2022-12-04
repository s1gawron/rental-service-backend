package com.s1gawron.rentalservice.address.dto.validator;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public enum AddressDTOValidator {

    I;

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern POLISH_POST_CODE_PATTERN = Pattern.compile("[0-9]{2}-[0-9]{3}");

    public boolean validate(final AddressDTO addressDTO) {
        if (addressDTO.getCountry() == null) {
            log.error("Country" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCountry();
        }

        if (addressDTO.getCity() == null) {
            log.error("City" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCity();
        }

        if (addressDTO.getStreet() == null) {
            log.error("Street" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForStreet();
        }

        if (addressDTO.getPostCode() == null) {
            log.error("Post code" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForPostCode();
        }

        final Matcher postCodeMatcher = POLISH_POST_CODE_PATTERN.matcher(addressDTO.getPostCode());

        if (!postCodeMatcher.matches()) {
            log.error("Provided post code does not match pattern: XX-XXX!!!");
            throw PostCodePatternViolationException.create(addressDTO.getPostCode());
        }

        return true;
    }

}
