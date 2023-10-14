package com.s1gawron.rentalservice.reservation.repository;

import com.s1gawron.rentalservice.reservation.model.ReservationHasTool;

import java.util.List;

public interface ReservationHasToolDAO {

    void saveAll(final List<ReservationHasTool> reservationHasTools);

}
