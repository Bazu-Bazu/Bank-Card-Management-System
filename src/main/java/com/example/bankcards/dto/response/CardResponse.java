package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CardResponse(
        Long id,
        String number,
        Long userId,
        LocalDate expirationDate,
        BigDecimal balance,
        CardStatus status
) {}
