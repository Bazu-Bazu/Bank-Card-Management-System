package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateTransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.security.userDetails.CustomUserDetails;
import com.example.bankcards.service.TransferService;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTransferController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtFilter jwtFilter;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("user1")
                .role(Role.USER)
                .enabled(true)
                .build();

        userDetails = new CustomUserDetails(user);
    }

    @BeforeEach
    void setUpSecurityContext() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createTransferShouldReturn201() throws Exception {
        TransferResponse response = TransferResponse.builder()
                .id(1L)
                .fromCardId(2L)
                .toCardId(3L)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(transferService.createTransfer(eq(userDetails.getUserId()), any(CreateTransferRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/user/transfers")
                        .principal(new TestingAuthenticationToken(userDetails, null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fromCardId": 2,
                                    "toCardId": 3,
                                    "amount": 100.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.fromCardId").value(response.fromCardId()))
                .andExpect(jsonPath("$.toCardId").value(response.toCardId()))
                .andExpect(jsonPath("$.amount").value(response.amount().doubleValue()));
    }

    @Test
    void getMyTransfersShouldReturn200() throws Exception {
        TransferResponse response = TransferResponse.builder()
                .id(1L)
                .fromCardId(2L)
                .toCardId(3L)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();

        Page<TransferResponse> page = new PageImpl<>(List.of(response));

        when(transferService.getAllTransfersByUserId(eq(userDetails.getUserId()), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/user/transfers")
                        .principal(new TestingAuthenticationToken(userDetails, null))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id()))
                .andExpect(jsonPath("$.content[0].fromCardId").value(response.fromCardId()))
                .andExpect(jsonPath("$.content[0].toCardId").value(response.toCardId()))
                .andExpect(jsonPath("$.content[0].amount").value(response.amount().doubleValue()));
    }

}
