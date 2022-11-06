package com.s1gawron.rentalservice.reservation.controller;

import com.s1gawron.rentalservice.reservation.exception.NoAccessForUserRoleException;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.user.controller.UserErrorHandlerController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

public abstract class ReservationErrorHandlerController extends UserErrorHandlerController {

    @ExceptionHandler(NoAccessForUserRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse noAccessForUserRoleException(final NoAccessForUserRoleException noAccessForUserRoleException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            noAccessForUserRoleException.getMessage(), httpServletRequest.getRequestURI());
    }

}
