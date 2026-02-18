package com.example.bankcards.service;

import com.example.bankcards.dto.request.LoginUserRequest;
import com.example.bankcards.dto.request.LogoutRequest;
import com.example.bankcards.dto.request.RefreshRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.IllegalRefreshTokenException;
import com.example.bankcards.exception.RefreshTokenExpiredException;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse login(LoginUserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        return generateTokens(request.username());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isRefreshToken(token)) {
            throw new IllegalRefreshTokenException(
                    String.format("Provided token %s is not a refresh token", token)
            );
        }

        RefreshToken refreshToken = refreshTokenService.findRefreshToken(token);
        if (!refreshToken.isActive()) {
            refreshTokenService.deleteTokenInNewTx(token);
            throw new RefreshTokenExpiredException(
                    String.format("Refresh token %s was expired", token)
            );
        }
        refreshTokenService.deleteToken(token);

        String username = jwtService.extractUsername(token);

        return generateTokens(username);
    }

    private AuthResponse generateTokens(String username) {
        User user = userService.findUserByUsername(username);

        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.addRefreshToken(user, newRefreshToken);

        return createAuthResponse(user.getId(), newAccessToken, newRefreshToken);
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.deleteToken(request.refreshToken());
    }

    private AuthResponse createAuthResponse(Long userId, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
