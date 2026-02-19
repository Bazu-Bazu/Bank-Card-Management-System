package com.example.bankcards.service;

import com.example.bankcards.dto.request.AddMoneyRequest;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AdminCanNotHaveCardException;
import com.example.bankcards.exception.CardAlreadyExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("user1")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void shouldCreateCardForUser() {
        CreateCardRequest request = new CreateCardRequest(
                user.getId(), "1234 5678 9012 3456", LocalDate.now().plusYears(3)
        );
        when(cardRepository.existsByNumber(request.cardNumber())).thenReturn(false);
        when(userService.findUserById(user.getId())).thenReturn(user);

        Card savedCard = Card.builder()
                .id(10L)
                .number(request.cardNumber())
                .user(user)
                .expirationDate(request.expirationDate())
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        CardResponse response = cardService.createCard(request);

        assertEquals(savedCard.getId(), response.id());
        assertEquals(savedCard.getUser().getId(), response.userId());
        assertTrue(response.number().endsWith(savedCard.getNumber().substring(15)));
    }

    @Test
    void shouldThrowWhenCardAlreadyExists() {
        when(cardRepository.existsByNumber(anyString())).thenReturn(true);

        CreateCardRequest request = new CreateCardRequest(
                user.getId(), "1234 5678 9012 3456", LocalDate.now().plusYears(3)
        );

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard(request));
    }

    @Test
    void shouldThrowWhenAdminTriesToCreateCard() {
        User admin = User.builder().id(2L).role(Role.ADMIN).build();
        when(userService.findUserById(admin.getId())).thenReturn(admin);
        when(cardRepository.existsByNumber(anyString())).thenReturn(false);

        CreateCardRequest request = new CreateCardRequest(
                admin.getId(), "1234 5678 9012 3456", LocalDate.now().plusYears(3)
        );

        assertThrows(AdminCanNotHaveCardException.class, () -> cardService.createCard(request));
    }

    @Test
    void shouldBlockCard() {
        Card card = Card.builder()
                .id(1L)
                .status(CardStatus.ACTIVE)
                .user(user)
                .number("1234 5678 9012 3456")
                .build();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        CardResponse response = cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, response.status());
    }

    @Test
    void shouldAddMoneyToCard() {
        Card card = Card.builder()
                .id(1L)
                .status(CardStatus.ACTIVE)
                .user(user)
                .balance(BigDecimal.valueOf(100))
                .number("1234 5678 9012 3456")
                .build();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        AddMoneyRequest request = new AddMoneyRequest(BigDecimal.valueOf(50));
        CardResponse response = cardService.addMoney(1L, request);

        assertEquals(BigDecimal.valueOf(150), response.balance());
    }

    @Test
    void shouldDebitMoney() {
        Card card = Card.builder().balance(BigDecimal.valueOf(200)).build();
        Card result = cardService.debitMoney(card, BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(150), result.getBalance());
    }

    @Test
    void shouldAddMoneyDirectly() {
        Card card = Card.builder().balance(BigDecimal.valueOf(100)).build();
        Card result = cardService.addMoney(card, BigDecimal.valueOf(25));
        assertEquals(BigDecimal.valueOf(125), result.getBalance());
    }

    @Test
    void shouldThrowWhenCardNotFound() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.findCardById(999L));
    }

}
