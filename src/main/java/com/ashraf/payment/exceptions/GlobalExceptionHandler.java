package com.ashraf.payment.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                Instant.now()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleXmlError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid ISO 20022 XML structure",
                request
        );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUsernameExists(
            UsernameAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessionAlreadyActiveException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleSessionActive(
            SessionAlreadyActiveException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                "Access denied: Admin role required",
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request
        );
    }
}