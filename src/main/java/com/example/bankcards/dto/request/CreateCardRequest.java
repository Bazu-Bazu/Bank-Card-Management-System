package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateCardRequest(

        @NotNull
        Long ownerId,

        @Pattern(
                regexp = "\\d{4} \\d{4} \\d{4} \\d{4}",
                message = "Card number must be in format 1234 5678 9101 1213"
        )
        String cardNumber,

        @NotNull
        @Future
        LocalDate expirationDate

) {}
