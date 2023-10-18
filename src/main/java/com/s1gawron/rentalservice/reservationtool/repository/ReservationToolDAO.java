package com.s1gawron.rentalservice.reservationtool.repository;

import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;

import java.util.List;

public interface ReservationToolDAO {

    void saveAll(final List<ReservationTool> reservationTools);

}
