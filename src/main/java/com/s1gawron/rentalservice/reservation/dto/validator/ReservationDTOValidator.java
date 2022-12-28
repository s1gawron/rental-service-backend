package com.s1gawron.rentalservice.reservation.dto.validator;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public enum ReservationDTOValidator {

    I;

    private static final String MESSAGE = " cannot be empty";

    public boolean validate(final ReservationDTO reservationDTO) {

        if (reservationDTO.getDateFrom() == null) {
            log.error("Date from" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForDateFrom();
        }

        final LocalDate currentDate = LocalDate.now();

        if (reservationDTO.getDateFrom().isBefore(currentDate)) {
            log.error("Date from is before current time!");
            throw DateMismatchException.createForDateFrom();
        }

        if (reservationDTO.getDateTo() == null) {
            log.error("Due date" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForDateTo();
        }

        if (reservationDTO.getDateTo().isBefore(currentDate)) {
            log.error("Date from is before current time!");
            throw DateMismatchException.createForDateTo();
        }

        if (reservationDTO.getDateFrom().isAfter(reservationDTO.getDateTo())) {
            log.error("Date from cannot be after due date!");
            throw DateMismatchException.createForDateFromIsAfterDueDate();
        }

        if (reservationDTO.getToolIds() == null || reservationDTO.getToolIds().isEmpty()) {
            log.error("Tools for reservation list" + MESSAGE);
            throw ReservationEmptyPropertiesException.createForToolsList();
        }

        return true;
    }

}
