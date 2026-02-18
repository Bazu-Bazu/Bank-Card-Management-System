package com.example.bankcards.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransferResponse(
        Long id,
        Long fromCardId,
        Long toCardId,
        BigDecimal amount,
        LocalDateTime createdAt
) {}
