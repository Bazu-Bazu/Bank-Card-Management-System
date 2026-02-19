package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtFilter jwtFilter;

    private UserResponse userResponse;

    @BeforeEach
    void setUpUserResponse() {
        userResponse = UserResponse.builder()
                .id(1L)
                .username("user1")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void createUserShouldReturn201() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "user1",
                                    "password": "User1234*"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.username").value(userResponse.username()))
                .andExpect(jsonPath("$.role").value(userResponse.role().name()))
                .andExpect(jsonPath("$.enabled").value(userResponse.enabled()));
    }

    @Test
    void disableUserShouldReturn200() throws Exception {
        when(userService.disableUser(eq(1L))).thenReturn(userResponse);

        mockMvc.perform(patch("/admin/users/disable/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()));
    }

    @Test
    void enableUserShouldReturn200() throws Exception {
        when(userService.enableUser(eq(1L))).thenReturn(userResponse);

        mockMvc.perform(patch("/admin/users/enable/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()));
    }

    @Test
    void getUserByIdShouldReturn200() throws Exception {
        when(userService.getUserById(eq(1L))).thenReturn(userResponse);

        mockMvc.perform(get("/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.id()));
    }

    @Test
    void getAllUsersShouldReturn200() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(userResponse.id()));
    }

    @Test
    void deleteUserShouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

}
