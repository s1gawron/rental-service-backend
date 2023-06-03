package com.s1gawron.rentalservice.user.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {

    ANONYMOUS,
    CUSTOMER,
    WORKER,
    ADMIN;

    public SimpleGrantedAuthority toSimpleGrantedAuthority() {
        if (this.equals(ANONYMOUS)) {
            return new SimpleGrantedAuthority("ROLE_ANONYMOUS");
        }

        return new SimpleGrantedAuthority(this.name());
    }

}
