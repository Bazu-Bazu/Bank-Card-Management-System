package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginUserRequest;
import com.example.bankcards.dto.request.LogoutRequest;
import com.example.bankcards.dto.request.RefreshRequest;
import com.example.bankcards.dto.response.AuthResponse;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtFilter jwtFilter;

    private AuthResponse authResponse;

    @BeforeEach
    void setUpAuthResponse() {
        authResponse = AuthResponse.builder()
                .userId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    @Test
    void loginShouldReturn200AndTokens() throws Exception {
        when(authService.login(any(LoginUserRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "User1234*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(authResponse.userId()))
                .andExpect(jsonPath("$.accessToken").value(authResponse.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.refreshToken()));
    }

    @Test
    void refreshShouldReturn200AndTokens() throws Exception {
        when(authService.refresh(any(RefreshRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "refreshToken"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(authResponse.userId()))
                .andExpect(jsonPath("$.accessToken").value(authResponse.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.refreshToken()));
    }

    @Test
    void logoutShouldReturn200() throws Exception {
        doNothing().when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "refreshToken"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
