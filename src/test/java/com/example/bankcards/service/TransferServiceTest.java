package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateTransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizationException;
import com.example.bankcards.exception.CardIsNotActive;
import com.example.bankcards.exception.NotEnoughMoneyOnTheCardException;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransferService transferService;

    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        fromCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .user(User.builder().id(1L).build())
                .expirationDate(LocalDate.of(2030, 1, 1))
                .build();

        toCard = Card.builder()
                .id(2L)
                .balance(BigDecimal.valueOf(500))
                .status(CardStatus.ACTIVE)
                .user(User.builder().id(1L).build())
                .expirationDate(LocalDate.of(2030, 1, 1))
                .build();
    }

    @Test
    void shouldCreateTransferSuccessfully() {
        CreateTransferRequest request = new CreateTransferRequest(
                1L, 2L, BigDecimal.valueOf(200)
        );

        when(cardService.findCardWithUser(1L)).thenReturn(fromCard);
        when(cardService.findCardWithUser(2L)).thenReturn(toCard);

        Transfer transfer = Transfer.builder()
                .id(10L)
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .build();

        when(transferRepository.save(any(Transfer.class)))
                .thenReturn(transfer);

        when(cardService.debitMoney(fromCard, BigDecimal.valueOf(200)))
                .thenReturn(fromCard);
        when(cardService.addMoney(toCard, BigDecimal.valueOf(200)))
                .thenReturn(toCard);

        TransferResponse response = transferService.createTransfer(1L, request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(fromCard.getId(), response.fromCardId());
        assertEquals(toCard.getId(), response.toCardId());

        verify(transferRepository).save(any(Transfer.class));
        verify(cardService).debitMoney(fromCard, BigDecimal.valueOf(200));
        verify(cardService).addMoney(toCard, BigDecimal.valueOf(200));
    }

    @Test
    void shouldThrowWhenNotEnoughMoney() {
        CreateTransferRequest request = new CreateTransferRequest(
                1L, 2L, BigDecimal.valueOf(2000)
        );

        when(cardService.findCardWithUser(1L)).thenReturn(fromCard);
        when(cardService.findCardWithUser(2L)).thenReturn(toCard);

        assertThrows(NotEnoughMoneyOnTheCardException.class, () ->
                transferService.createTransfer(1L, request)
        );

        verify(transferRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCardIsNotActive() {
        fromCard.setStatus(CardStatus.BLOCKED);

        CreateTransferRequest request = new CreateTransferRequest(
                1L, 2L, BigDecimal.valueOf(100)
        );

        when(cardService.findCardWithUser(1L)).thenReturn(fromCard);
        when(cardService.findCardWithUser(2L)).thenReturn(toCard);

        assertThrows(CardIsNotActive.class, () ->
                transferService.createTransfer(1L, request)
        );

        verify(transferRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserNotOwner() {
        fromCard.setUser(User.builder().id(3L).build());

        CreateTransferRequest request = new CreateTransferRequest(
                1L, 2L, BigDecimal.valueOf(100)
        );

        when(cardService.findCardWithUser(1L)).thenReturn(fromCard);
        when(cardService.findCardWithUser(2L)).thenReturn(toCard);

        assertThrows(AuthorizationException.class, () ->
                transferService.createTransfer(1L, request)
        );

        verify(transferRepository, never()).save(any());
    }

    @Test
    void shouldReturnTransfersByUserId() {
        Transfer transfer = Transfer.builder()
                .id(1L)
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();

        Page<Transfer> page = new PageImpl<>(List.of(transfer));

        when(transferRepository.findAllByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        Page<TransferResponse> response = transferService.getAllTransfersByUserId(
                1L, PageRequest.of(0, 10)
        );

        assertEquals(1, response.getTotalElements());
    }

    @Test
    void shouldReturnAllTransfers() {
        Transfer transfer = Transfer.builder()
                .id(1L)
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();

        Page<Transfer> page = new PageImpl<>(List.of(transfer));

        when(transferRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<TransferResponse> response = transferService.getAllTransfers(
                PageRequest.of(0, 10)
        );

        assertEquals(1, response.getTotalElements());
    }

}
