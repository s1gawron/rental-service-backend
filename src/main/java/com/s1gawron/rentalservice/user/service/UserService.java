package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Transactional
    public UserDTO validateAndRegisterUser(final UserRegisterDTO userRegisterDTO) {
        userRegisterDTO.validate();

        final Optional<User> userEmailExistOptional = getUser(userRegisterDTO.getEmail());

        if (userEmailExistOptional.isPresent()) {
            throw UserEmailExistsException.create();
        }

        final String encryptedPassword = new BCryptPasswordEncoder().encode(userRegisterDTO.getPassword());
        final User user = User.createUser(userRegisterDTO, encryptedPassword);

        userRepository.save(user);

        return user.toUserDTO();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(final String email) {
        return userRepository.findByEmail(email);
    }
}
