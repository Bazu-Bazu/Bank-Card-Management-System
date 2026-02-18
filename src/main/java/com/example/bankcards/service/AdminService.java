package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createAdmin(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(
                    String.format("User with name %s already exists", request.username())
            );
        }

        User newAdmin = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .build();

        User savedAdmin = userRepository.save(newAdmin);

        return createUserResponse(savedAdmin);
    }

    private UserResponse createUserResponse(User admin) {
        return UserResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .enabled(admin.getEnabled())
                .build();
    }

}
