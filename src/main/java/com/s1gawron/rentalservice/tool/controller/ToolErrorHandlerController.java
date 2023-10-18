package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.shared.AbstractErrorHandlerController;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolStateTypeDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

public abstract class ToolErrorHandlerController extends AbstractErrorHandlerController {

    @ExceptionHandler(ToolEmptyPropertiesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolEmptyPropertiesExceptionHandler(final ToolEmptyPropertiesException toolEmptyPropertiesException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolEmptyPropertiesException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ToolCategoryDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolCategoryDoesNotExistExceptionHandler(final ToolCategoryDoesNotExistException toolCategoryDoesNotExistException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolCategoryDoesNotExistException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ToolStateTypeDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolStateTypeDoesNotExistExceptionHandler(final ToolStateTypeDoesNotExistException toolStateTypeDoesNotExistException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolStateTypeDoesNotExistException.getMessage(), httpServletRequest.getRequestURI());
    }

}
