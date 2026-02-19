package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AddMoneyRequest;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.CardService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtFilter jwtFilter;

    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        cardResponse = CardResponse.builder()
                .id(1L)
                .number("**** **** **** 1234")
                .userId(1L)
                .expirationDate(LocalDate.now().plusYears(3))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void createCardShouldReturn201() throws Exception {
        when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(post("/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "ownerId": 1,
                                    "cardNumber": "1234 5678 1234 1234",
                                    "expirationDate": "2029-02-18"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cardResponse.id()))
                .andExpect(jsonPath("$.number").value(cardResponse.number()))
                .andExpect(jsonPath("$.userId").value(cardResponse.userId()))
                .andExpect(jsonPath("$.status").value(cardResponse.status().name()))
                .andExpect(jsonPath("$.balance").value(cardResponse.balance().doubleValue()));
    }

    @Test
    void blockCardShouldReturn200() throws Exception {
        when(cardService.blockCard(1L)).thenReturn(cardResponse);

        mockMvc.perform(patch("/admin/cards/block/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardResponse.id()));
    }

    @Test
    void unlockCardShouldReturn200() throws Exception {
        when(cardService.unlockCard(1L)).thenReturn(cardResponse);

        mockMvc.perform(patch("/admin/cards/unlock/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardResponse.id()));
    }

    @Test
    void addMoneyShouldReturn200() throws Exception {
        when(cardService.addMoney(eq(1L), any(AddMoneyRequest.class))).thenReturn(cardResponse);

        mockMvc.perform(patch("/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 500.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardResponse.id()))
                .andExpect(jsonPath("$.balance").value(cardResponse.balance().doubleValue()));
    }

    @Test
    void getAllCardsShouldReturn200() throws Exception {
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        when(cardService.getAllCards(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardResponse.id()));
    }

    @Test
    void getCardsByUserIdShouldReturn200() throws Exception {
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        when(cardService.getCardsByUserId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/cards/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardResponse.id()));
    }

    @Test
    void deleteCardShouldReturn204() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/admin/cards/1"))
                .andExpect(status().isNoContent());
    }

}
