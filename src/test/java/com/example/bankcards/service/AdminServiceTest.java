package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Test
    void createAdminShouldSaveAdmin() {
        CreateUserRequest request = new CreateUserRequest("admin1", "Admin1234*");

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        User savedAdmin = User.builder()
                .id(1L)
                .username(request.username())
                .password("encodedPassword")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedAdmin);

        UserResponse response = adminService.createAdmin(request);

        assertEquals(1L, response.id());
        assertEquals("admin1", response.username());
        assertEquals(Role.ADMIN, response.role());
        assertTrue(response.enabled());
    }

    @Test
    void createAdminShouldThrowIfUserExists() {
        CreateUserRequest request = new CreateUserRequest("admin1", "Admin1234*");

        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> adminService.createAdmin(request));
    }

}
