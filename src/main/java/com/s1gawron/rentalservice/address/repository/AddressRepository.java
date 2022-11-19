package com.s1gawron.rentalservice.address.repository;

import com.s1gawron.rentalservice.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
