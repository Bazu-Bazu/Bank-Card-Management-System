package com.example.bankcards.service;

import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RefreshTokenNotFoundException;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("user1")
                .build();
    }

    @Test
    void shouldAddRefreshToken() {
        String token = "refreshToken";
        when(jwtService.getRefreshTokenExpiration()).thenReturn(refreshTokenExpiration);

        refreshTokenService.addRefreshToken(user, token);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertEquals(token, saved.getToken());
        assertEquals(user, saved.getUser());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getExpiresAt());
    }

    @Test
    void shouldFindExistingRefreshToken() {
        String token = "existingToken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.findRefreshToken(token);

        assertEquals(refreshToken, result);
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        String token = "notExistingToken";

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class, () ->
                refreshTokenService.findRefreshToken(token)
        );
    }

    @Test
    void shouldDeleteToken() {
        String token = "tokenToDelete";

        refreshTokenService.deleteToken(token);
        refreshTokenService.deleteTokenInNewTx(token);

        verify(refreshTokenRepository, times(2)).deleteToken(token);
    }

}
