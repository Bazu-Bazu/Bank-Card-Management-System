package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddMoneyRequest(

        @NotNull
        BigDecimal amount

) {}
