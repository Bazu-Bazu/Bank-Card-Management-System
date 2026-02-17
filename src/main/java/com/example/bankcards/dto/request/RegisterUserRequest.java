package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Pattern;

public record RegisterUserRequest(

        @Pattern(regexp = "^[A-Za-z0-9_]{5,50}$")
        String username,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$")
        String password

) {}
