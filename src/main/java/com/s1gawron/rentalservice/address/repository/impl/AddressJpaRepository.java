package com.s1gawron.rentalservice.address.repository.impl;

import com.s1gawron.rentalservice.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

interface AddressJpaRepository extends JpaRepository<Address, Long> {

}
