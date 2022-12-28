package com.s1gawron.rentalservice.reservation.dto.validator;

import com.s1gawron.rentalservice.reservation.dto.ReservationDTO;
import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationDTOValidatorTest {

    @Test
    void shouldValidate() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(2L), "Comment", List.of(1L, 2L, 3L));
        assertTrue(ReservationDTOValidator.I.validate(reservationDTO));
    }

    @Test
    void shouldThrowExceptionWhenDateFromIsNull() {
        final ReservationDTO reservationDTO = new ReservationDTO(null, LocalDate.parse("2022-12-16"), "Comment", List.of(1L, 2L, 3L));

        assertThrows(ReservationEmptyPropertiesException.class, () -> ReservationDTOValidator.I.validate(reservationDTO), "Date from cannot be null!");
    }

    @Test
    void shouldThrowExceptionWhenDateFromIsBeforeCurrentDate() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now().minusDays(3L), LocalDate.parse("2022-12-16"), "Comment", List.of(1L, 2L, 3L));

        assertThrows(DateMismatchException.class, () -> ReservationDTOValidator.I.validate(reservationDTO), "Date from cannot be before current date!");
    }

    @Test
    void shouldThrowExceptionWhenDateToIsNull() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), null, "Comment", List.of(1L, 2L, 3L));

        assertThrows(ReservationEmptyPropertiesException.class, () -> ReservationDTOValidator.I.validate(reservationDTO), "Due date cannot be null!");
    }

    @Test
    void shouldThrowExceptionWhenDateToIsBeforeCurrentDate() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().minusDays(3L), "Comment", List.of(1L, 2L, 3L));

        assertThrows(DateMismatchException.class, () -> ReservationDTOValidator.I.validate(reservationDTO), "Due date cannot be before current date!");
    }

    @Test
    void shouldThrowExceptionWhenDateFromIsAfterDueDate() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now().plusDays(1L), LocalDate.now(), "Comment", List.of(1L, 2L, 3L));

        assertThrows(DateMismatchException.class, () -> ReservationDTOValidator.I.validate(reservationDTO), "Date from cannot be after due date!");
    }

    @Test
    void shouldThrowExceptionWhenToolIdListIsNull() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(2L), "Comment", null);

        assertThrows(ReservationEmptyPropertiesException.class, () -> ReservationDTOValidator.I.validate(reservationDTO),
            "Tools must be added to reservation!");
    }

    @Test
    void shouldThrowExceptionWhenToolIdListIsEmpty() {
        final ReservationDTO reservationDTO = new ReservationDTO(LocalDate.now(), LocalDate.now().plusDays(2L), "Comment", List.of());

        assertThrows(ReservationEmptyPropertiesException.class, () -> ReservationDTOValidator.I.validate(reservationDTO),
            "Tools must be added to reservation!");
    }

}