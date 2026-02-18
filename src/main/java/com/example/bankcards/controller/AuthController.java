package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginUserRequest;
import com.example.bankcards.dto.request.LogoutRequest;
import com.example.bankcards.dto.request.RefreshRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Operations available to all")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login in account")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginUserRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request) {
        AuthResponse response = authService.refresh(request);

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "Logout from account")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);

        return ResponseEntity.status(200).body(null);
    }

}
