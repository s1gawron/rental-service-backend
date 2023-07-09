package com.s1gawron.rentalservice.reservation.repository.impl;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationHasToolJpaRepository extends JpaRepository<ReservationHasTool, Long> {

}
