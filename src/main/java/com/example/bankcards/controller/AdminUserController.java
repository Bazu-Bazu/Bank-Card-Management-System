package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.createUser(request);

        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/disable/{userId}")
    public ResponseEntity<?> disableUser(@PathVariable("userId") @Valid Long userId) {
        UserResponse response = userService.disableUser(userId);

        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/enable/{userId}")
    public ResponseEntity<?> enableUser(@PathVariable("userId") @Valid Long userId) {
        UserResponse response = userService.enableUser(userId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") @Valid Long userId) {
        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        Page<UserResponse> responses = userService.getAllUsers(pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") @Valid Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.status(204).body(null);
    }

}
