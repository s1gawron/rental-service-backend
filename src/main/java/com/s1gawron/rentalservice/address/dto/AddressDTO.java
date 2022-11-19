package com.s1gawron.rentalservice.address.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Log4j2
@Getter
@Builder
@JsonDeserialize(builder = AddressDTO.AddressDTOBuilder.class)
public class AddressDTO {

    private static final String MESSAGE = " was left empty in registration process";

    private static final Pattern POLISH_POST_CODE_PATTERN = Pattern.compile("[0-9]{2}-[0-9]{3}");

    private final String country;

    private final String city;

    private final String street;

    private final String postCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressDTOBuilder {

    }

    public boolean validate() {
        if (this.country == null) {
            log.error("Country" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCountry();
        }

        if (this.city == null) {
            log.error("City" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForCity();
        }

        if (this.street == null) {
            log.error("Street" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForStreet();
        }

        if (this.postCode == null) {
            log.error("Post code" + MESSAGE);
            throw AddressRegisterEmptyPropertiesException.createForPostCode();
        }

        final Matcher postCodeMatcher = POLISH_POST_CODE_PATTERN.matcher(this.postCode);

        if (!postCodeMatcher.matches()) {
            log.error("Provided post code does not match pattern: XX-XXX!!!");
            throw PostCodePatternViolationException.create(this.postCode);
        }

        return true;
    }

}
