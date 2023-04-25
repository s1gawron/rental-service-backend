package com.s1gawron.rentalservice.user.helper;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;

public enum UserCreatorHelper {

    I;

    private static final String EMAIL = "test@test.pl";

    public User createCustomer() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterRequest, "encryptedPassword");

        user.setCustomerAddress(Address.from(addressDTO));

        return user;
    }

    public User createWorker() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(EMAIL, "Start00!", "John", "Kowalski", UserRole.WORKER, addressDTO);
        final User user = User.createUser(userRegisterRequest, "encryptedPassword");

        user.setCustomerAddress(Address.from(addressDTO));

        return user;
    }

    public User createDifferentCustomer() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Przedmiescia", "01-100");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest("test2@test.pl", "Start00!", "George", "Happs", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterRequest, "encryptedPassword");

        user.setCustomerAddress(Address.from(addressDTO));

        return user;
    }
}
