package com.s1gawron.rentalservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum UserRole {

    CUSTOMER("customer"),

    WORKER("worker");

    private final String name;

    public static Optional<UserRole> findByValue(final String role) {
        return Arrays.stream(values()).filter(value -> value.name.equalsIgnoreCase(role)).findFirst();
    }

}
