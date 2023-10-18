package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationStatus;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {

    Reservation save(final Reservation reservation);

    Page<Reservation> findAllByCustomer(final User customer, final Pageable pageable);

    Optional<Reservation> findByReservationIdAndCustomer(final Long reservationId, final User customer);

    Optional<Reservation> findByReservationId(final long reservationId);

    List<Long> getReservationIdsWithDateToOlderThanAndStatus(LocalDateTime dateTime, ReservationStatus reservationStatus);

}
