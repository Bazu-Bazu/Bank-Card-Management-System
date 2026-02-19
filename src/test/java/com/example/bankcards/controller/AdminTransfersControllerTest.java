package com.example.bankcards.controller;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.security.jwt.JwtFilter;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTransfersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminTransfersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtFilter jwtFilter;

    private TransferResponse transferResponse;

    @BeforeEach
    void setUpTransferResponse() {
        transferResponse = TransferResponse.builder()
                .id(1L)
                .fromCardId(2L)
                .toCardId(3L)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllTransfersByUserIdShouldReturn200() throws Exception {
        Page<TransferResponse> page = new PageImpl<>(List.of(transferResponse));

        when(transferService.getAllTransfersByUserId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/transfers/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(transferResponse.id()))
                .andExpect(jsonPath("$.content[0].fromCardId").value(transferResponse.fromCardId()))
                .andExpect(jsonPath("$.content[0].toCardId").value(transferResponse.toCardId()))
                .andExpect(jsonPath("$.content[0].amount").value(transferResponse.amount().doubleValue()));
    }

    @Test
    void getAllTransfersShouldReturn200() throws Exception {
        Page<TransferResponse> page = new PageImpl<>(List.of(transferResponse));

        when(transferService.getAllTransfers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/transfers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(transferResponse.id()))
                .andExpect(jsonPath("$.content[0].fromCardId").value(transferResponse.fromCardId()))
                .andExpect(jsonPath("$.content[0].toCardId").value(transferResponse.toCardId()))
                .andExpect(jsonPath("$.content[0].amount").value(transferResponse.amount().doubleValue()));
    }

}
