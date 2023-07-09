package com.s1gawron.rentalservice.address.repository.impl;

import com.s1gawron.rentalservice.address.model.Address;
import com.s1gawron.rentalservice.address.repository.AddressDAO;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAddressDAO implements AddressDAO {

    private final AddressJpaRepository addressJpaRepository;

    public JpaAddressDAO(final AddressJpaRepository addressJpaRepository) {
        this.addressJpaRepository = addressJpaRepository;
    }

    @Override public Address save(final Address address) {
        return addressJpaRepository.save(address);
    }

}
