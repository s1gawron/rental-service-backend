package com.s1gawron.rentalservice.address.service;

import com.s1gawron.rentalservice.address.dto.AddressDTO;
import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.repository.AddressRepository;
import com.s1gawron.rentalservice.user.model.UserType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional
    public Optional<Address> validateAndSaveAddress(final AddressDTO addressDTO, final UserType userType) {
        //No need to save user address for worker
        if (userType == UserType.WORKER) {
            return Optional.empty();
        }

        if (addressDTO == null) {
            throw AddressRegisterEmptyPropertiesException.create();
        }

        addressDTO.validate();

        final Address address = addressRepository.save(Address.from(addressDTO));
        return Optional.of(address);
    }

}
