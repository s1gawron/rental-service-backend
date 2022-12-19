package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.user.exception.UserRoleDoesNotExistException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum UserRole {

    CUSTOMER("CUSTOMER"),

    WORKER("WORKER");

    private final String name;

    public static UserRole findByValue(final String role) {
        return Arrays.stream(values()).filter(value -> value.name.equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> UserRoleDoesNotExistException.create(role));
    }

}
