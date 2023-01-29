package com.s1gawron.rentalservice.reservation.dto.validator;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public enum ReservationDTOValidator {

    I;

    private static final Logger log = LoggerFactory.getLogger(ReservationDTOValidator.class);

    private static final String MESSAGE = " cannot be empty";

    public boolean validate(final ReservationDTO reservationDTO) {

        if (reservationDTO.dateFrom() == null) {
            log.error("Date from" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForDateFrom();
        }

        final LocalDate currentDate = LocalDate.now();

        if (reservationDTO.dateFrom().isBefore(currentDate)) {
            log.error("Date from is before current time!");
            throw DateMismatchException.createForDateFrom();
        }

        if (reservationDTO.dateTo() == null) {
            log.error("Due date" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForDateTo();
        }

        if (reservationDTO.dateTo().isBefore(currentDate)) {
            log.error("Date from is before current time!");
            throw DateMismatchException.createForDateTo();
        }

        if (reservationDTO.dateFrom().isAfter(reservationDTO.dateTo())) {
            log.error("Date from cannot be after due date!");
            throw DateMismatchException.createForDateFromIsAfterDueDate();
        }

        if (reservationDTO.toolIds() == null || reservationDTO.toolIds().isEmpty()) {
            log.error("Tools for reservation list" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForToolsList();
        }

        return true;
    }

}
