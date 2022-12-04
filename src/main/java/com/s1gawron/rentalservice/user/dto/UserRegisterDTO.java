package com.s1gawron.rentalservice.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.s1gawron.rentalservice.address.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@JsonDeserialize(builder = UserRegisterDTO.UserRegisterDTOBuilder.class)
public class UserRegisterDTO {

    private final String email;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final String userRole;

    private final AddressDTO address;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserRegisterDTOBuilder {

    }

}
