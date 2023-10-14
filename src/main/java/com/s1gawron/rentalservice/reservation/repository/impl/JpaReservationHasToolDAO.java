package com.s1gawron.rentalservice.reservation.repository.impl;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;
import com.s1gawron.rentalservice.reservation.repository.ReservationHasToolDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaReservationHasToolDAO implements ReservationHasToolDAO {

    private final ReservationHasToolJpaRepository reservationHasToolJpaRepository;

    public JpaReservationHasToolDAO(final ReservationHasToolJpaRepository reservationHasToolJpaRepository) {
        this.reservationHasToolJpaRepository = reservationHasToolJpaRepository;
    }

    @Override public void saveAll(final List<ReservationHasTool> reservationHasTools) {
        reservationHasToolJpaRepository.saveAll(reservationHasTools);
    }
}
