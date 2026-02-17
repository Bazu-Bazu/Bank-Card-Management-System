package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        if (cardRepository.existsByNumber(request.cardNumber())) {
            throw new CardAlreadyExistsException(
                    String.format("Card %s already exists", request.cardNumber())
            );
        }

        User user = userService.findUserById(request.ownerId());

        Card newCard = Card.builder()
                .number(request.cardNumber())
                .user(user)
                .expirationDate(request.expirationDate())
                .build();

        Card savedCard = cardRepository.save(newCard);

        return createCardResponse(savedCard);
    }

    @Transactional
    public CardResponse blockCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(CardStatus.BLOCKED);

        Card savedCard = cardRepository.save(card);

        return createCardResponse(savedCard);
    }

    @Transactional
    public CardResponse unlockCard(Long cardId) {
        Card card = findCardById(cardId);
        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);

        return createCardResponse(savedCard);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
         return cardRepository.findAll(pageable)
                 .map(this::createCardResponse);
    }

    public Page<CardResponse> getCardsByUserId(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable)
                .map(this::createCardResponse);
    }

    private Card findCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Card %d not found", id)
                ));
    }

    private CardResponse createCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .number(maskCardNumber(card.getNumber()))
                .userId(card.getUser().getId())
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }

    private String maskCardNumber(String cardNumber) {
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

}
