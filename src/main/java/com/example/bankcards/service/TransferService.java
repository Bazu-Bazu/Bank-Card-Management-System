package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateTransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.AuthorizationException;
import com.example.bankcards.exception.CardIsNotActive;
import com.example.bankcards.exception.NotEnoughMoneyOnTheCardException;
import com.example.bankcards.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final CardService cardService;

    @Transactional
    public TransferResponse createTransfer(Long userId, CreateTransferRequest request) {
        Card from = cardService.findCardWithUser(request.fromCardId());
        Card to = cardService.findCardWithUser(request.toCardId());
        BigDecimal amount = request.amount();

        validateUserIsOwnerOfCards(userId, from, to);
        validateCardsIsActive(from, to);
        validateIsEnoughMoneyOnCard(from, amount);

        Transfer transfer = Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);
        Card savedFrom = cardService.debitMoney(from, amount);
        Card savedTo = cardService.addMoney(to, amount);

        return createTransferResponse(savedTransfer, savedFrom.getId(), savedTo.getId());
    }

    private void validateUserIsOwnerOfCards(Long userId, Card from, Card to) {
        Long cardId = 0L;

        if (!from.getUser().getId().equals(userId)) {
            cardId = from.getId();
        } else if (!to.getUser().getId().equals(userId)) {
            cardId = to.getId();
        }

        if (!cardId.equals(0L)) {
            throw new AuthorizationException(
                    String.format("User %d is not the owner of the card %d", userId, cardId)
            );
        }
    }

    private void validateCardsIsActive(Card from, Card to) {
        Long cardId = 0L;

        if (!from.isActive()) {
            cardId = from.getId();
        } else if (!to.isActive()) {
            cardId = to.getId();
        }

        if (!cardId.equals(0L)) {
            throw new CardIsNotActive(
                    String.format("Card %d is not active", cardId)
            );
        }
    }

    private void validateIsEnoughMoneyOnCard(Card from, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughMoneyOnTheCardException(
                    String.format("Not enough money on the card %d. There's %s on the card, but need %s",
                            from.getId(), from.getBalance(), amount)
            );
        }
    }

    public Page<TransferResponse> getAllTransfersByUserId(Long userId, Pageable pageable) {
        return transferRepository.findAllByUserId(userId, pageable)
                .map(this::createTransferResponse);
    }

    public Page<TransferResponse> getAllTransfers(Pageable pageable) {
        return transferRepository.findAll(pageable)
                .map(this::createTransferResponse);
    }

    private TransferResponse createTransferResponse(Transfer transfer, Long fromId, Long toId) {
        return TransferResponse.builder()
                .id(transfer.getId())
                .fromCardId(fromId)
                .toCardId(toId)
                .amount(transfer.getAmount())
                .createdAt(transfer.getCreatedAt())
                .build();
    }

    private TransferResponse createTransferResponse(Transfer transfer) {
        return TransferResponse.builder()
                .id(transfer.getId())
                .fromCardId(transfer.getFromCard().getId())
                .toCardId(transfer.getToCard().getId())
                .amount(transfer.getAmount())
                .createdAt(transfer.getCreatedAt())
                .build();
    }

}
