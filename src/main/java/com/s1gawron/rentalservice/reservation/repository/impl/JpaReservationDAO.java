package com.s1gawron.rentalservice.reservation.repository.impl;

import com.s1gawron.rentalservice.reservation.model.Reservation;
import com.s1gawron.rentalservice.reservation.repository.ReservationDAO;
import com.s1gawron.rentalservice.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaReservationDAO implements ReservationDAO {

    private final ReservationJpaRepository reservationJpaRepository;

    public JpaReservationDAO(final ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override public Reservation save(final Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override public List<Reservation> findAllById(final List<Long> reservationIds) {
        return reservationJpaRepository.findAllById(reservationIds);
    }

    @Override public List<Reservation> findAllByCustomer(final User customer) {
        return reservationJpaRepository.findAllByCustomer(customer);
    }

    @Override public Optional<Reservation> findByReservationIdAndCustomer(final Long reservationId, final User customer) {
        return reservationJpaRepository.findByReservationIdAndCustomer(reservationId, customer);
    }

    @Override public List<Long> getAllIds() {
        return reservationJpaRepository.getAllIds();
    }

    @Override public Optional<Reservation> findByReservationId(final long reservationId) {
        return reservationJpaRepository.findByReservationId(reservationId);
    }

}
