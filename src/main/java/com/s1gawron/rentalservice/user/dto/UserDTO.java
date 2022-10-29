package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonDeserialize(builder = UserDTO.UserDTOBuilder.class)
public class UserDTO {

    private final String firstName;

    private final String lastName;

    private final String email;

    private final AddressDTO customerAddress;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserDTOBuilder {

    }
}
