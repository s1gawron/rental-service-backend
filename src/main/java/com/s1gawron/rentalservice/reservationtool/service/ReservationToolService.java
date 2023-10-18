package com.s1gawron.rentalservice.reservationtool.service;

import com.s1gawron.rentalservice.reservationtool.model.ReservationTool;
import com.s1gawron.rentalservice.reservationtool.repository.ReservationToolDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationToolService {

    private final ReservationToolDAO reservationToolDAO;

    public ReservationToolService(final ReservationToolDAO reservationToolDAO) {
        this.reservationToolDAO = reservationToolDAO;
    }

    @Transactional
    public void saveAll(final List<ReservationTool> reservationTools) {
        reservationToolDAO.saveAll(reservationTools);
    }
}
