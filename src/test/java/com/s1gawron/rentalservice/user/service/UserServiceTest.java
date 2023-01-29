package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.service.AddressService;
import com.s1gawron.rentalservice.shared.UserNotFoundException;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static final String EMAIL = "test@test.pl";

    private SecurityContext securityContextMock;

    private UserRepository userRepositoryMock;

    private AddressService addressServiceMock;

    private UserService userService;

    @BeforeEach
    void setUp() {
        final Authentication authentication = Mockito.mock(Authentication.class);
        securityContextMock = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(EMAIL);
        SecurityContextHolder.setContext(securityContextMock);

        userRepositoryMock = Mockito.mock(UserRepository.class);
        addressServiceMock = Mockito.mock(AddressService.class);
        userService = new UserService(userRepositoryMock, addressServiceMock);
    }

    @Test
    void shouldFindUserByEmail() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(customer));

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
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", "CUSTOMER", addressDTO);
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
    void shouldThrowExceptionWhenEmailAlreadyExistsWhileRegisteringUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", "CUSTOMER", addressDTO);
        final User user = User.createUser(userRegisterDTO, UserRole.CUSTOMER, "encryptedPassword");

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(UserEmailExistsException.class, () -> userService.validateAndRegisterUser(userRegisterDTO));
    }

    @Test
    void shouldGetUserDetails() {
        final User customer = UserCreatorHelper.I.createCustomer();

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(customer));

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
    void shouldThrowExceptionWhenUserIsNotFoundWhileGettingUserDetails() {
        assertThrows(UserNotFoundException.class, userService::getUserDetails, "User: " + EMAIL + " could not be found!");
    }

}