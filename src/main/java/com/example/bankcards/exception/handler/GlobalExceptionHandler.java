package com.example.bankcards.exception.handler;

import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleBadRequest(UserAlreadyExistsException e, HttpServletRequest request) {
        String message = "Validation Error: " + e.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequest(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        String message = "Validation Error: " + errors;
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler({
            CardAlreadyExistsException.class,
            NotEnoughMoneyOnTheCardException.class,
            CardIsNotActive.class,
            AdminCanNotHaveCardException.class,
            UserIsAdminException.class,
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleUnauthorized(BadCredentialsException e, HttpServletRequest request) {
        String message = "Invalid username or password";
        return buildResponse(HttpStatus.UNAUTHORIZED, message, request);
    }

    @ExceptionHandler({
            IllegalRefreshTokenException.class,
            RefreshTokenExpiredException.class,
            AuthorizationException.class
    })
    public ResponseEntity<?> handleUnauthorized(RuntimeException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            RefreshTokenNotFoundException.class,
            CardNotFoundException.class,
    })
    public ResponseEntity<?> handleNotFound(RuntimeException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(UserIsNotEnabledException.class)
    public ResponseEntity<?> handleForbidden(UserIsNotEnabledException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

}
