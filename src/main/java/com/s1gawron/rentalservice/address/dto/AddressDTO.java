package com.s1gawron.rentalservice.address.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = AddressDTO.AddressDTOBuilder.class)
public class AddressDTO {

    private final String country;

    private final String city;

    private final String street;

    private final String postCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressDTOBuilder {

    }

}
