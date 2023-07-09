package com.s1gawron.rentalservice.address.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.repository.AddressDAO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressServiceTest {

    private AddressDAO addressDAO;

    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressDAO = Mockito.mock(AddressDAO.class);
        addressService = new AddressService(addressDAO);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserRoleIsWorker() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");

        final Optional<Address> result = addressService.validateAndSaveAddress(addressDTO, UserRole.WORKER);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAddressDTOIsNull() {
        assertThrows(AddressRegisterEmptyPropertiesException.class, () -> addressService.validateAndSaveAddress(null, UserRole.CUSTOMER),
            "Address cannot be empty!");
    }

    @Test
    void shouldValidateAndSaveAddress() {
        final AddressDTO addressDTO = new AddressDTO("Poland", "Warsaw", "Test", "01-000");
        final Address address = Address.from(addressDTO);

        Mockito.when(addressDAO.save(Mockito.any(Address.class))).thenReturn(address);

        final Optional<Address> result = addressService.validateAndSaveAddress(addressDTO, UserRole.CUSTOMER);

        Mockito.verify(addressDAO, Mockito.times(1)).save(Mockito.any(Address.class));
        assertTrue(result.isPresent());
        assertEquals("Poland", result.get().getCountry());
        assertEquals("Warsaw", result.get().getCity());
        assertEquals("Test", result.get().getStreet());
        assertEquals("01-000", result.get().getPostCode());
    }

}