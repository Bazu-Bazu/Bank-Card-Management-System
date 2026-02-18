package com.example.bankcards.exception;

public class NotEnoughMoneyOnTheCardException extends RuntimeException {

    public NotEnoughMoneyOnTheCardException(String message) {
        super(message);
    }

}
