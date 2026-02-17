package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> createAdmin() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);

        return ResponseEntity.status(200).body(null);
    }

}
