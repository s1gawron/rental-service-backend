package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.user.exception.UserRoleDoesNotExistException;

import java.util.Arrays;

public enum UserRole {

    CUSTOMER,

    WORKER;

    public static UserRole findByValue(final String role) {
        return Arrays.stream(values()).filter(value -> value.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> UserRoleDoesNotExistException.create(role));
    }

}
