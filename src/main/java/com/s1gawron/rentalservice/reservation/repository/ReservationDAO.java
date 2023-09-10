package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {

    Reservation save(final Reservation reservation);

    List<Reservation> findAllByCustomer(final User customer);

    Optional<Reservation> findByReservationIdAndCustomer(final Long reservationId, final User customer);

    Optional<Reservation> findByReservationId(final long reservationId);

    List<Long> getReservationIdsForDateToOlderThan(LocalDateTime dateTime);

}
