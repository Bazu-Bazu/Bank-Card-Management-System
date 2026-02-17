package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(

        @NotBlank
        String username,

        @NotBlank
        String password

) {}
