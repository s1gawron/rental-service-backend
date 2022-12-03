package com.s1gawron.rentalservice.tool.controller;

import com.s1gawron.rentalservice.shared.AbstractGeneralErrorHandlerController;
import com.s1gawron.rentalservice.shared.ErrorResponse;
import com.s1gawron.rentalservice.tool.exception.ToolCategoryDoesNotExistException;
import com.s1gawron.rentalservice.tool.exception.ToolEmptyPropertiesException;
import com.s1gawron.rentalservice.tool.exception.ToolNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

public abstract class ToolErrorHandlerController extends AbstractGeneralErrorHandlerController {

    @ExceptionHandler(ToolNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse toolNotFoundExceptionHandler(final ToolNotFoundException toolNotFoundException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
            toolNotFoundException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ToolEmptyPropertiesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolEmptyPropertiesExceptionHandler(final ToolEmptyPropertiesException toolEmptyPropertiesException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolEmptyPropertiesException.getMessage(), httpServletRequest.getRequestURI());
    }

    @ExceptionHandler(ToolCategoryDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse toolCategoryDoesNotExistExceptionExceptionHandler(final ToolCategoryDoesNotExistException toolCategoryDoesNotExistException,
        final HttpServletRequest httpServletRequest) {
        return new ErrorResponse(Instant.now().toString(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
            toolCategoryDoesNotExistException.getMessage(), httpServletRequest.getRequestURI());
    }

}
