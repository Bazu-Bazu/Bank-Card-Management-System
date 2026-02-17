package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody @Valid CreateCardRequest request) {
        CardResponse response = cardService.createCard(request);

        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/block/{cardId}")
    public ResponseEntity<?> blockCard(@PathVariable("cardId") @Valid Long cardId) {
        CardResponse response = cardService.blockCard(cardId);

        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/unlock/{cardId}")
    public ResponseEntity<?> unlockCard(@PathVariable("cardId") @Valid Long cardId) {
        CardResponse response = cardService.unlockCard(cardId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCards(Pageable pageable) {
        Page<CardResponse> responses = cardService.getAllCards(pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCardsByUserId(
            @PathVariable("userId") @Valid Long userId,
            Pageable pageable) {
        Page<CardResponse> responses = cardService.getCardsByUserId(userId, pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @DeleteMapping("{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable("cardId") @Valid Long cardId) {
        cardService.deleteCard(cardId);

        return ResponseEntity.status(200).body(null);
    }

}
