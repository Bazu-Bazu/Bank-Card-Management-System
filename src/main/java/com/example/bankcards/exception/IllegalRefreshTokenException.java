package com.example.bankcards.exception;

public class IllegalRefreshTokenException extends RuntimeException {

    public IllegalRefreshTokenException(String message) {
        super(message);
    }

}
