package com.s1gawron.rentalservice.reservation.repository.impl;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.model.ReservationStatus;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByCustomer(final User customer, final Pageable pageable);

    Optional<Reservation> findByReservationId(final Long reservationId);

    Optional<Reservation> findByReservationIdAndCustomer(final Long reservationId, final User customer);

    @Query(value = "SELECT reservationId FROM Reservation WHERE dateTo < :dateTime AND reservationStatus = :reservationStatus")
    List<Long> getReservationIdsWithDateToOlderThanAndStatus(@Param(value = "dateTime") final LocalDateTime dateTime,
        @Param(value = "reservationStatus") final ReservationStatus reservationStatus);

}
