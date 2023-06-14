package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.service.AddressService;
import com.s1gawron.rentalservice.shared.exception.UserNotFoundException;
import com.s1gawron.rentalservice.shared.exception.UserUnauthenticatedException;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
import com.s1gawron.rentalservice.user.exception.WorkerRegisteredByNonAdminUserException;
import com.s1gawron.rentalservice.user.helper.UserCreatorHelper;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static final String EMAIL = "test@test.pl";

    private Authentication authenticationMock;

    private UserRepository userRepositoryMock;

    private AddressService addressServiceMock;

    private UserService userService;

    @BeforeEach
    void setUp() {
        authenticationMock = Mockito.mock(Authentication.class);
        final SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);

        userRepositoryMock = Mockito.mock(UserRepository.class);
        addressServiceMock = Mockito.mock(AddressService.class);
        userService = new UserService(userRepositoryMock, addressServiceMock, new BCryptPasswordEncoder());
    }

    @Test
    void shouldFindUserByEmail() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(userRepositoryMock.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        final Optional<User> result = userService.getUserByEmail(EMAIL);

        assertTrue(result.isPresent());
        assertEquals(EMAIL, result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Kowalski", result.get().getLastName());
        assertEquals(UserRole.CUSTOMER, result.get().getUserRole());
        assertEquals("Poland", result.get().getCustomerAddress().getCountry());
        assertEquals("01-000", result.get().getCustomerAddress().getPostCode());
    }

    @Test
    void shouldNotFindUser() {
        final Optional<User> result = userService.getUserByEmail(EMAIL);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldValidateAndRegisterUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final Address address = Address.from(addressDTO);

        Mockito.when(addressServiceMock.validateAndSaveAddress(addressDTO, UserRole.CUSTOMER)).thenReturn(Optional.of(address));

        final UserDTO result = userService.validateAndRegisterUser(userRegisterDTO);

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
        assertEquals(EMAIL, result.email());
        assertEquals("John", result.firstName());
        assertEquals("Kowalski", result.lastName());
        assertEquals("Poland", result.customerAddress().country());
        assertEquals("01-000", result.customerAddress().postCode());
    }

    @Test
    void shouldThrowExceptionWhenWorkerIsNotRegisteredByAdmin() {
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.WORKER, null);

        assertThrows(WorkerRegisteredByNonAdminUserException.class, () -> userService.validateAndRegisterUser(userRegisterDTO));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsWhileRegisteringUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterDTO, "encryptedPassword");

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(UserEmailExistsException.class, () -> userService.validateAndRegisterUser(userRegisterDTO));
    }

    @Test
    void shouldGetUserDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);
        Mockito.when(userRepositoryMock.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        final UserDTO result = userService.getUserDetails();

        assertNotNull(result);
        assertEquals(EMAIL, result.email());
        assertEquals("John", result.firstName());
        assertEquals("Kowalski", result.lastName());
        assertEquals(UserRole.CUSTOMER.name(), result.userRole());
        assertEquals("Poland", result.customerAddress().country());
        assertEquals("01-000", result.customerAddress().postCode());
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNullWhileGettingUserDetails() {
        assertThrows(UserUnauthenticatedException.class, userService::getUserDetails, "User is not authenticated!");
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFoundWhileGettingUserDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(authenticationMock.getPrincipal()).thenReturn(customer);

        assertThrows(UserNotFoundException.class, userService::getUserDetails, "User: " + EMAIL + " could not be found!");
    }

}