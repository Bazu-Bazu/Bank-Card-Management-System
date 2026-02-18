package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.security.userDetails.CustomUserDetails;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/cards")
@RequiredArgsConstructor
@Tag(name = "User Cards API", description = "Operations available for USER role")
@SecurityRequirement(name = "bearerAuth")
public class UserCardController {

    private final CardService cardService;

    @Operation(summary = "Get personal cards with pageable")
    @GetMapping
    public ResponseEntity<?> getMyCards(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        Page<CardResponse> responses = cardService.getCardsByUserId(userDetails.getUserId(), pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @Operation(summary = "Block card")
    @PatchMapping("/{cardId}")
    public ResponseEntity<?> blockedMyCard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("cardId") @Valid Long cardId
    ) {
        CardResponse response = cardService.blockedCardByOwner(userDetails.getUserId(), cardId);

        return ResponseEntity.status(200).body(response);
    }

}
