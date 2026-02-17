package com.example.bankcards.dto.response;

import com.example.bankcards.entity.Role;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String username,
        Role role,
        Boolean enabled
) {}
