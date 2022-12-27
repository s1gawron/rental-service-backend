package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByCustomer(final User customer);

    Optional<Reservation> findByReservationId(final Long reservationId);

}
