package com.s1gawron.rentalservice.address.repository.impl;

import com.s1gawron.rentalservice.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaRepository extends JpaRepository<Address, Long> {

}
