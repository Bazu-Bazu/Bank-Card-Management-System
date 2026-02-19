package com.example.bankcards.service;

import com.example.bankcards.dto.request.LoginUserRequest;
import com.example.bankcards.dto.request.LogoutRequest;
import com.example.bankcards.dto.request.RefreshRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.IllegalRefreshTokenException;
import com.example.bankcards.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("user1")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void loginShouldReturnTokens() {
        LoginUserRequest request = new LoginUserRequest("user1", "User1234*");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(userService.findUserByUsername("user1")).thenReturn(user);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        doNothing().when(refreshTokenService).addRefreshToken(any(), anyString());

        AuthResponse response = authService.login(request);

        assertEquals(user.getId(), response.userId());
        assertEquals("accessToken", response.accessToken());
        assertEquals("refreshToken", response.refreshToken());
    }

    @Test
    void refreshShouldReturnNewTokens() {
        String oldRefreshToken = "oldRefreshToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(60));

        RefreshRequest request = new RefreshRequest(oldRefreshToken);

        when(jwtService.isRefreshToken(oldRefreshToken)).thenReturn(true);
        when(refreshTokenService.findRefreshToken(oldRefreshToken)).thenReturn(refreshToken);
        lenient().doNothing().when(refreshTokenService).deleteTokenInNewTx(oldRefreshToken);
        lenient().doNothing().when(refreshTokenService).deleteToken(oldRefreshToken);
        when(jwtService.extractUsername(oldRefreshToken)).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(user);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("newRefreshToken");
        doNothing().when(refreshTokenService).addRefreshToken(any(), anyString());

        AuthResponse response = authService.refresh(request);

        assertEquals("newAccessToken", response.accessToken());
        assertEquals("newRefreshToken", response.refreshToken());
        assertEquals(user.getId(), response.userId());
    }

    @Test
    void refreshShouldThrowIllegalRefreshToken() {
        String token = "notARefreshToken";
        RefreshRequest request = new RefreshRequest(token);
        when(jwtService.isRefreshToken(token)).thenReturn(false);

        assertThrows(IllegalRefreshTokenException.class, () -> authService.refresh(request));
    }

    @Test
    void logoutShouldCallDeleteToken() {
        String token = "refreshToken";
        LogoutRequest request = new LogoutRequest(token);

        doNothing().when(refreshTokenService).deleteToken(token);

        authService.logout(request);

        verify(refreshTokenService, times(1)).deleteToken(token);
    }

}
