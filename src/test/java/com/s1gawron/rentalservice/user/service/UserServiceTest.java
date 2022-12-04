package com.s1gawron.rentalservice.user.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.service.AddressService;
import com.s1gawron.rentalservice.user.dto.UserDTO;
import com.s1gawron.rentalservice.user.dto.UserRegisterDTO;
import com.s1gawron.rentalservice.user.exception.UserEmailExistsException;
import com.s1gawron.rentalservice.user.model.User;
import com.s1gawron.rentalservice.user.model.UserRole;
import com.s1gawron.rentalservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static final String EMAIL = "test@test.pl";

    private UserRepository userRepositoryMock;

    private AddressService addressServiceMock;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        addressServiceMock = Mockito.mock(AddressService.class);
        userService = new UserService(userRepositoryMock, addressServiceMock);
    }

    @Test
    void shouldFindUserByEmail() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterDTO, "encryptedPassword");

        user.setCustomerAddress(Address.from(addressDTO));
        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(user));

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
    void shouldDeleteUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterDTO, "encryptedPassword");

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        userService.deleteUser(EMAIL);

        Mockito.verify(userRepositoryMock, Mockito.times(1)).delete(user);
    }

    @Test
    void shouldValidateAndRegisterUser() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final Address address = Address.from(addressDTO);

        Mockito.when(addressServiceMock.validateAndSaveAddress(addressDTO, UserRole.CUSTOMER)).thenReturn(Optional.of(address));

        final UserDTO result = userService.validateAndRegisterUser(userRegisterDTO);

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
        assertEquals(EMAIL, result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Kowalski", result.getLastName());
        assertEquals("Poland", result.getCustomerAddress().getCountry());
        assertEquals("01-000", result.getCustomerAddress().getPostCode());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(EMAIL, "Start00!", "John", "Kowalski", UserRole.CUSTOMER, addressDTO);
        final User user = User.createUser(userRegisterDTO, "encryptedPassword");

        Mockito.when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(UserEmailExistsException.class, () -> userService.validateAndRegisterUser(userRegisterDTO));
    }

}