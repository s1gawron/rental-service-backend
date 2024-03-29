package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.service.AddressService;
import com.s1gawron.rentalservice.shared.exception.UserNotFoundException;
import com.s1gawron.rentalservice.shared.usercontext.UserContextProvider;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.dto.validator.UserDTOValidator;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
import com.s1gawron.rentalservice.user.exception.WorkerRegisteredByNonAdminUserException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;

    private final AddressService addressService;

    private final PasswordEncoder passwordEncoder;

    public UserService(final UserDAO userDAO, final AddressService addressService, final PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO validateAndRegisterUser(final UserRegisterDTO userRegisterDTO) {
        UserDTOValidator.I.validate(userRegisterDTO);

        if (userRegisterDTO.userRole().equals(UserRole.WORKER) && isNotInvokedByAdmin()) {
            throw WorkerRegisteredByNonAdminUserException.create();
        }

        final Optional<User> userEmailExistOptional = getUserByEmail(userRegisterDTO.email());

        if (userEmailExistOptional.isPresent()) {
            throw UserEmailExistsException.create();
        }

        final String encryptedPassword = passwordEncoder.encode(userRegisterDTO.password());
        final User user = User.createFrom(userRegisterDTO, encryptedPassword);

        addressService.validateAndSaveAddress(userRegisterDTO.address(), userRegisterDTO.userRole()).ifPresent(user::setCustomerAddress);
        userDAO.save(user);

        return user.toUserDTO();
    }

    @Transactional
    public void saveUser(final User user) {
        Optional.ofNullable(user.getCustomerAddress()).ifPresent(addressService::saveAddress);
        userDAO.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(final String email) {
        return userDAO.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDetails() {
        final User loggedInUser = UserContextProvider.I.getLoggedInUser();
        final User user = getUserByEmail(loggedInUser.getEmail()).orElseThrow(() -> UserNotFoundException.create(loggedInUser.getEmail()));

        return user.toUserDTO();
    }

    private boolean isNotInvokedByAdmin() {
        return !UserContextProvider.I.hasUserRole(UserRole.ADMIN);
    }

}
