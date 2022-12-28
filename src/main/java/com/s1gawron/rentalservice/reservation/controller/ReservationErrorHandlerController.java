package com.s1gawron.rentalservice.reservation.controller;

import com.s1gawron.rentalservice.reservation.exception.DateMismatchException;
import com.s1gawron.rentalservice.reservation.exception.ReservationEmptyPropertiesException;
import com.s1gawron.rentalservice.reservation.exception.ReservationNotFoundException;
import com.s1gawron.rentalservice.shared.AbstractErrorHandlerController;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.tool.exception.ToolUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

public abstract class ReservationErrorHandlerController extends AbstractErrorHandlerController {

    @ExceptionHandler(ToolUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolUnavailableExceptionHandler(final ToolUnavailableException toolUnavailableException, final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolUnavailableException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reservationNotFoundExceptionHandler(final ReservationNotFoundException reservationNotFoundException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
            reservationNotFoundException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ReservationEmptyPropertiesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reservationEmptyPropertiesExceptionHandler(final ReservationEmptyPropertiesException reservationEmptyPropertiesException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            reservationEmptyPropertiesException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(DateMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dateMismatchExceptionHandler(final DateMismatchException dateMismatchException, final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            dateMismatchException.getMessage(), httpServletRequest.getRequestURI());
    }

}
