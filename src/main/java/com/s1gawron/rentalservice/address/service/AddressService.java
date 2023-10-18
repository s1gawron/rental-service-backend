package com.s1gawron.rentalservice.address.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.dto.validator.AddressDTOValidator;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.repository.AddressDAO;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressDAO addressDAO;

    public AddressService(final AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    @Transactional
    public Optional<Address> validateAndSaveAddress(final AddressDTO addressDTO, final UserRole userRole) {
        //No need to save user address for worker
        if (userRole == UserRole.WORKER) {
            return Optional.empty();
        }

        if (addressDTO == null) {
            throw AddressRegisterEmptyPropertiesException.create();
        }

        AddressDTOValidator.I.validate(addressDTO);

        final Address address = addressDAO.save(Address.from(addressDTO));
        return Optional.of(address);
    }

    @Transactional
    public void saveAddress(final Address address) {
        addressDAO.save(address);
    }

}
