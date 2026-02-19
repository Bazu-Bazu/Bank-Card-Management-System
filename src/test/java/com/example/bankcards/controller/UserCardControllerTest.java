package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.security.userDetails.CustomUserDetails;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

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
    void getMyCardsShouldReturnPage() throws Exception {
        CardResponse card = CardResponse.builder()
                .id(2L)
                .number("**** **** **** 1234")
                .userId(userDetails.getUserId())
                .expirationDate(LocalDate.of(2030, 12, 31))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .build();

        Page<CardResponse> page = new PageImpl<>(List.of(card));

        when(cardService.getCardsByUserId(eq(userDetails.getUserId()), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/user/cards")
                        .principal(new TestingAuthenticationToken(userDetails, null))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(card.id()))
                .andExpect(jsonPath("$.content[0].number").value(card.number()))
                .andExpect(jsonPath("$.content[0].userId").value(card.userId()))
                .andExpect(jsonPath("$.content[0].status").value(card.status().name()))
                .andExpect(jsonPath("$.content[0].balance").value(card.balance().doubleValue()));
    }

    @Test
    void blockedMyCardShouldReturnUpdatedCard() throws Exception {
        Long cardId = 2L;

        CardResponse updatedCard = CardResponse.builder()
                .id(cardId)
                .number("**** **** **** 1234")
                .userId(userDetails.getUserId())
                .expirationDate(LocalDate.of(2030, 12, 31))
                .status(CardStatus.BLOCKED)
                .balance(new BigDecimal("1000.00"))
                .build();

        when(cardService.blockedCardByOwner(userDetails.getUserId(), cardId))
                .thenReturn(updatedCard);

        mockMvc.perform(patch("/user/cards/{cardId}", cardId)
                        .principal(new TestingAuthenticationToken(userDetails, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCard.id()))
                .andExpect(jsonPath("$.number").value(updatedCard.number()))
                .andExpect(jsonPath("$.userId").value(updatedCard.userId()))
                .andExpect(jsonPath("$.status").value(updatedCard.status().name()))
                .andExpect(jsonPath("$.balance").value(updatedCard.balance().doubleValue()));
    }

}
