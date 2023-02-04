package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.service.AddressService;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.shared.UserUnauthenticatedException;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterRequest;
import com.s1gawron.rentalservice.user.dto.validator.UserDTOValidator;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AddressService addressService;

    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final AddressService addressService, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO validateAndRegisterUser(final UserRegisterRequest userRegisterRequest) {
        UserDTOValidator.I.validate(userRegisterRequest);

        final Optional<User> userEmailExistOptional = getUserByEmail(userRegisterRequest.email());

        if (userEmailExistOptional.isPresent()) {
            throw UserEmailExistsException.create();
        }

        final String encryptedPassword = passwordEncoder.encode(userRegisterRequest.password());
        final UserRole userRole = UserRole.findByValue(userRegisterRequest.userRole());
        final User user = User.createUser(userRegisterRequest, userRole, encryptedPassword);

        addressService.validateAndSaveAddress(userRegisterRequest.address(), userRole).ifPresent(user::setCustomerAddress);
        userRepository.save(user);

        return user.toUserDTO();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDetails() {
        final Optional<Object> principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        final User principalToUser = principal.map(p -> (User) p).orElseThrow(UserUnauthenticatedException::create);
        final User user = getUserByEmail(principalToUser.getEmail()).orElseThrow(() -> UserNotFoundException.create(principalToUser.getEmail()));

        return user.toUserDTO();
    }

    @Transactional
    public void saveCustomerWithReservation(final User customer) {
        userRepository.save(customer);
    }
}
