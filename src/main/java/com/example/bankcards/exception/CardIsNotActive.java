package com.example.bankcards.exception;

public class CardIsNotActive extends RuntimeException {

    public CardIsNotActive(String message) {
        super(message);
    }

}
