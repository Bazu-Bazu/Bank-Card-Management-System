package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AddMoneyRequest;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
@Tag(name = "Admin Cards API", description = "Operations available for ADMIN role")
@SecurityRequirement(name = "bearerAuth")
public class AdminCardController {

    private final CardService cardService;

    @Operation(summary = "Create new card for user")
    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody @Valid CreateCardRequest request) {
        CardResponse response = cardService.createCard(request);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Block card")
    @PatchMapping("/block/{cardId}")
    public ResponseEntity<?> blockCard(@PathVariable("cardId") @Valid Long cardId) {
        CardResponse response = cardService.blockCard(cardId);

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "Unlock card")
    @PatchMapping("/unlock/{cardId}")
    public ResponseEntity<?> unlockCard(@PathVariable("cardId") @Valid Long cardId) {
        CardResponse response = cardService.unlockCard(cardId);

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "Add money to the card")
    @PatchMapping("/{cardId}")
    public ResponseEntity<?> addMoney(
            @PathVariable("cardId") @Valid Long cardId,
            @RequestBody @Valid AddMoneyRequest request
    ) {
        CardResponse response = cardService.addMoney(cardId, request);

        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "Get all cards with pageable")
    @GetMapping
    public ResponseEntity<?> getAllCards(Pageable pageable) {
        Page<CardResponse> responses = cardService.getAllCards(pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @Operation(summary = "Get cards by user with pageable")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCardsByUserId(
            @PathVariable("userId") @Valid Long userId,
            Pageable pageable) {
        Page<CardResponse> responses = cardService.getCardsByUserId(userId, pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @Operation(summary = "Delete card")
    @DeleteMapping("{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable("cardId") @Valid Long cardId) {
        cardService.deleteCard(cardId);

        return ResponseEntity.status(204).body(null);
    }

}
