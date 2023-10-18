package com.s1gawron.rentalservice.user.controller;

import com.s1gawron.rentalservice.address.exception.AddressRegisterEmptyPropertiesException;
import com.s1gawron.rentalservice.address.exception.PostCodePatternViolationException;
import com.s1gawron.rentalservice.shared.AbstractErrorHandlerController;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.user.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

public abstract class UserErrorHandlerController extends AbstractErrorHandlerController {

    @ExceptionHandler(UserEmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse userEmailExistsExceptionHandler(final UserEmailExistsException userEmailExistsException, final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),
            userEmailExistsException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(UserRegisterEmptyPropertiesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userEmptyRegisterPropertiesExceptionHandler(final UserRegisterEmptyPropertiesException userRegisterEmptyPropertiesException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            userRegisterEmptyPropertiesException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(UserEmailPatternViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userEmailPatternViolationExceptionHandler(final UserEmailPatternViolationException userEmailPatternViolationException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            userEmailPatternViolationException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(UserPasswordTooWeakException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userPasswordTooWeakExceptionHandler(final UserPasswordTooWeakException userPasswordTooWeakException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            userPasswordTooWeakException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(AddressRegisterEmptyPropertiesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse addressEmptyRegisterPropertiesExceptionHandler(final AddressRegisterEmptyPropertiesException addressRegisterEmptyPropertiesException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            addressRegisterEmptyPropertiesException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(PostCodePatternViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse postCodePatternViolationExceptionHandler(final PostCodePatternViolationException postCodePatternViolationException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            postCodePatternViolationException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(WorkerRegisteredByNonAdminUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse workerRegisteredByNonAdminUserExceptionHandler(final WorkerRegisteredByNonAdminUserException workerRegisteredByNonAdminUserException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            workerRegisteredByNonAdminUserException.getMessage(), httpServletRequest.getRequestURI());
    }

}
