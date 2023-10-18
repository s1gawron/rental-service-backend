package com.s1gawron.rentalservice.user.dto;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.user.model.UserRole;

public record UserRegisterDTO(String email, String password, String firstName, String lastName, UserRole userRole,
                              AddressDTO address) {

}
