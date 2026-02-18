package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateTransferRequest(

        @NotBlank
        Long fromCardId,

        @NotBlank
        Long toCardId,

        @NotBlank
        BigDecimal amount

) {}
