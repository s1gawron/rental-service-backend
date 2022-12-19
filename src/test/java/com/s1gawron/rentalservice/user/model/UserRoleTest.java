package com.s1gawron.rentalservice.user.model;

import com.s1gawron.rentalservice.user.exception.UserRoleDoesNotExistException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void shouldFindRole() {
        final UserRole userRole = UserRole.findByValue("customer");

        assertNotNull(userRole);
        assertEquals(UserRole.CUSTOMER, userRole);
    }

    @Test
    void shouldNotFindRole() {
        assertThrows(UserRoleDoesNotExistException.class, () -> UserRole.findByValue("doesNotExist"), "Role: doesNotExist does not exist!");
    }

}