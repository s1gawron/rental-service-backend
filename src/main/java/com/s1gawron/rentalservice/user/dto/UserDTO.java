package com.s1gawron.rentalservice.user.dto;

import com.s1gawron.rentalservice.address.dto.AddressDTO;

public record UserDTO(String firstName, String lastName, String email, String userRole, AddressDTO customerAddress) {

}
