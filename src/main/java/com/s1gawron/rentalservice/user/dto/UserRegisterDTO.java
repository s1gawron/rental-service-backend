package com.s1gawron.rentalservice.user.dto;

import com.s1gawron.rentalservice.address.dto.AddressDTO;

public record UserRegisterDTO(String email, String password, String firstName, String lastName, String userRole, AddressDTO address) {

}
