package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationHasToolRepository extends JpaRepository<ReservationHasTool, Long> {

}
