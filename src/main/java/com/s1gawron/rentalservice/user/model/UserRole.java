package com.s1gawron.rentalservice.user.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {

    ANONYMOUS,
    CUSTOMER,
    WORKER,
    ADMIN;

    private static final String ANONYMOUS_ROLE_NAME = "ROLE_ANONYMOUS";

    public SimpleGrantedAuthority toSimpleGrantedAuthority() {
        if (this.equals(ANONYMOUS)) {
            return new SimpleGrantedAuthority(ANONYMOUS_ROLE_NAME);
        }

        return new SimpleGrantedAuthority(this.name());
    }

    public static UserRole getUserRole(final String name) {
        if (name.equals(ANONYMOUS_ROLE_NAME)) {
            return ANONYMOUS;
        }

        return valueOf(name);
    }

}
