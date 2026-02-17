package com.example.bankcards.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        Long userId,
        String accessToken,
        String refreshToken
) {}
