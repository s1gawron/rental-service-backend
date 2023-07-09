package com.s1gawron.rentalservice.user.repository.impl;

import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
