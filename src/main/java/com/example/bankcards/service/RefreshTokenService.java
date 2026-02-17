package com.example.bankcards.service;

import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RefreshTokenNotFoundException;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional(propagation = Propagation.MANDATORY)
    public void addRefreshToken(User user, String token) {
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setCreatedAt(Instant.now());

        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findRefreshToken(String encodedToken) {
        return refreshTokenRepository.findByToken(encodedToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException(
                        String.format("Refresh token %s not found", encodedToken)
                ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTokenInNewTx(String token) {
        refreshTokenRepository.deleteToken(token);
    }

    @Transactional
    public void deleteToken(String token) {
        refreshTokenRepository.deleteToken(token);
    }

}
