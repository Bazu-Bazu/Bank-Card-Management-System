package com.example.bankcards.exception;

public class UserIsAdminException extends RuntimeException {

    public UserIsAdminException(String message) {
        super(message);
    }

}
