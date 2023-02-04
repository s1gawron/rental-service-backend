package com.s1gawron.rentalservice.shared;

import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

public abstract class AbstractErrorHandlerController {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundExceptionHandler(final UserNotFoundException userNotFoundException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
            userNotFoundException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(NoAccessForUserRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse noAccessForUserRoleException(final NoAccessForUserRoleException noAccessForUserRoleException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            noAccessForUserRoleException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(UserUnauthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse userUnauthorizedExceptionExceptionHandler(final UserUnauthenticatedException userUnauthenticatedException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            userUnauthenticatedException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ToolNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse toolNotFoundExceptionHandler(final ToolNotFoundException toolNotFoundException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
            toolNotFoundException.getMessage(), httpServletRequest.getRequestURI());
    }

}
