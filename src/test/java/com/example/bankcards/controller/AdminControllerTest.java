package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.AdminService;
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

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtFilter jwtFilter;

    private UserResponse adminResponse;

    @BeforeEach
    void setUpUserResponse() {
        adminResponse = UserResponse.builder()
                .id(1L)
                .username("admin1")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void createAdminShouldReturn201() throws Exception {
        when(adminService.createAdmin(any(CreateUserRequest.class))).thenReturn(adminResponse);

        mockMvc.perform(post("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "admin1",
                                    "password": "Admin1234*"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(adminResponse.id()))
                .andExpect(jsonPath("$.username").value(adminResponse.username()))
                .andExpect(jsonPath("$.role").value(adminResponse.role().name()))
                .andExpect(jsonPath("$.enabled").value(adminResponse.enabled()));
    }

}
