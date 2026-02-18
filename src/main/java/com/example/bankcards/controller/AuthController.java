package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginUserRequest;
import com.example.bankcards.dto.request.LogoutRequest;
import com.example.bankcards.dto.request.RefreshRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginUserRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request) {
        AuthResponse response = authService.refresh(request);

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);

        return ResponseEntity.status(200).body(null);
    }

}
