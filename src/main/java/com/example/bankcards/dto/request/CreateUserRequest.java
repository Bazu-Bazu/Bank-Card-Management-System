package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(

        @Pattern(
                regexp = "^[A-Za-z0-9_]{5,50}$",
                message = "Username must be 5-50 characters and contain only letters, numbers and underscores"
        )
        String username,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
                message = "Password must be 8-50 characters and contain lowercase and uppercase letters, " +
                        "numbers and special characters."
        )
        String password

) {}
