package com.s1gawron.rentalservice.user.repository;

import com.s1gawron.rentalservice.user.model.User;

import java.util.Optional;

public interface UserDAO {

    Optional<User> findByEmail(final String email);

    User save(final User user);

    void deleteAll();
    
}
