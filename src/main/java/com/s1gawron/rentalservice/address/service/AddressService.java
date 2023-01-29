package com.s1gawron.rentalservice.address.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.dto.validator.AddressDTOValidator;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.repository.AddressRepository;
import com.s1gawron.rentalservice.user.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(final AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
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

        final Address address = addressRepository.save(Address.from(addressDTO));
        return Optional.of(address);
    }

}
