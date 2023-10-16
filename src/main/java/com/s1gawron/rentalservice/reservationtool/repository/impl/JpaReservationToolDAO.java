package com.s1gawron.rentalservice.reservationtool.repository.impl;

import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;
import com.s1gawron.rentalservice.reservationtool.repository.ReservationToolDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaReservationToolDAO implements ReservationToolDAO {

    private final ReservationToolJpaRepository reservationToolJpaRepository;

    public JpaReservationToolDAO(final ReservationToolJpaRepository reservationToolJpaRepository) {
        this.reservationToolJpaRepository = reservationToolJpaRepository;
    }

    @Override public void saveAll(final List<ReservationTool> reservationTools) {
        reservationToolJpaRepository.saveAll(reservationTools);
    }
}
