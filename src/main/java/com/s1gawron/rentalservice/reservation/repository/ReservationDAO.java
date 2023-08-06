package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReservationDAO {

    Reservation save(final Reservation reservation);

    List<Reservation> findAllById(final List<Long> reservationIds);

    List<Reservation> findAllByCustomer(final User customer);

    Optional<Reservation> findByReservationIdAndCustomer(final Long reservationId, final User customer);

    @Query(value = "SELECT reservationId from Reservation")
    List<Long> getAllIds();

    Optional<Reservation> findByReservationId(final long reservationId);

}
