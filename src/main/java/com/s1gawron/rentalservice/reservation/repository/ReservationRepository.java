package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
