package com.company.auth.exceptions;

import com.company.auth.dtos.error.ErrorResponse;
import com.company.auth.enums.ResponseErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException resourceNotFoundException,
            HttpServletRequest request
    ){
        ResponseErrorCodes errorCodes = resourceNotFoundException.getResponseErrorCodes();
        ErrorResponse errorResponse = new ErrorResponse(
                errorCodes.getCode(),
                errorCodes.getErrorMsg(),
                request.getRequestURI(),
                LocalDateTime.now()

        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialException(
            InvalidCredentialException invalidCredentialException,
            HttpServletRequest request
    ){
        ResponseErrorCodes errorCodes = invalidCredentialException.getResponseErrorCodes();
        ErrorResponse errorResponse = new ErrorResponse(
                errorCodes.getCode(),
                errorCodes.getErrorMsg(),
                request.getRequestURI(),
                LocalDateTime.now()

        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                5000,
                "Internal server error",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


