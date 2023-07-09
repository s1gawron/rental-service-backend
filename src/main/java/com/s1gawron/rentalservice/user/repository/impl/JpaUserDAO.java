package com.s1gawron.rentalservice.user.repository.impl;

import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserDAO implements UserDAO {

    private final UserJpaRepository userJpaRepository;

    public JpaUserDAO(final UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override public Optional<User> findByEmail(final String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override public User save(final User user) {
        return userJpaRepository.save(user);
    }

    @Override public void deleteAll() {
        userJpaRepository.deleteAll();
    }

}
