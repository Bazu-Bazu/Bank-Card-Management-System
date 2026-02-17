package com.example.bankcards.exception;

public class UserIsNotEnabledException extends RuntimeException {

    public UserIsNotEnabledException(String message) {
        super(message);
    }

}
