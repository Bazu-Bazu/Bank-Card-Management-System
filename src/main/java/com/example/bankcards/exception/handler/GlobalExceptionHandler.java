package com.example.bankcards.exception.handler;

import com.example.bankcards.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handlerException(UserAlreadyExistsException e) {
        String message = "Validation Error: " + e.getMessage();
        return ResponseEntity.status(400).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerException(MethodArgumentNotValidException e) {
        String message = "Validation Error: " +
                "1) Username must be 5-50 characters and contain only letters, numbers and underscores. " +
                "2) Password must be 8-50 characters and contain lowercase and uppercase letters, " +
                "numbers and special characters.";
        return ResponseEntity.status(400).body(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handlerException(BadCredentialsException e) {
        String message = "Invalid username or password";
        return ResponseEntity.status(401).body(message);
    }

    @ExceptionHandler(IllegalRefreshTokenException.class)
    public ResponseEntity<?> handlerException(IllegalRefreshTokenException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<?> handlerException(RefreshTokenExpiredException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handlerException(UserNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<?> handlerException(RefreshTokenNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

}
