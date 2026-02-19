package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserIsAdminException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("user1")
                .password("User1234*")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("user1", "User1234*");

        when(userRepository.existsByUsername("user1"))
                .thenReturn(false);

        when(passwordEncoder.encode("User1234*"))
                .thenReturn("encodedPass");

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("user1", response.username());
        assertEquals(Role.USER, response.role());

        verify(passwordEncoder).encode("User1234*");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("user1", "User1234*");

        when(userRepository.existsByUsername("user1"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.createUser(request)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDisableUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.disableUser(1L);

        assertFalse(response.enabled());
    }

    @Test
    void shouldThrowWhenDisableAdmin() {
        user.setRole(Role.ADMIN);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThrows(UserIsAdminException.class, () ->
                userService.disableUser(1L)
        );
    }

    @Test
    void shouldEnableUser() {
        user.setEnabled(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.enableUser(1L);

        assertTrue(response.enabled());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.findUserById(1L)
        );
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals("user1", response.username());
    }

    @Test
    void shouldReturnPagedUsers() {
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<UserResponse> response =
                userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

}
