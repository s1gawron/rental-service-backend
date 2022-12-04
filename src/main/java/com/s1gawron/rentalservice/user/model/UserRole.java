package com.s1gawron.rentalservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {

    CUSTOMER("customer"),

    WORKER("worker");

    private final String name;

}
