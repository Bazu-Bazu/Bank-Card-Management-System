package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserIsAdminException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(
                    String.format("User with name %s already exists", request.username())
            );
        }

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(newUser);

        return createUserResponse(savedUser);
    }

    @Transactional
    public UserResponse disableUser(Long userId) {
        User user = findUserById(userId);

        validateUserIsNotAdmin(user);

        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        return createUserResponse(savedUser);
    }

    @Transactional
    public UserResponse enableUser(Long userId) {
        User user = findUserById(userId);

        validateUserIsNotAdmin(user);

        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        return createUserResponse(savedUser);
    }

    private void validateUserIsNotAdmin(User user) {
        if (user.getRole().equals(Role.ADMIN)) {
            throw new UserIsAdminException(
                    String.format("User %d has role ADMIN", user.getId())
            );
        }
    }

    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);

        return createUserResponse(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::createUserResponse);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with name %s not found", username)
                ));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User %d not found", id)
                ));
    }

    private UserResponse createUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }

}
