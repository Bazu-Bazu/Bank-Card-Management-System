package com.example.bankcards.controller;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/transfers")
@RequiredArgsConstructor
@Tag(name = "Admin Transfers API", description = "Operations available for ADMIN role")
@SecurityRequirement(name = "bearerAuth")
public class AdminTransfersController {

    private final TransferService transferService;

    @Operation(summary = "Get transfers by user with pageable")
    @GetMapping("{userId}")
    public ResponseEntity<?> getAllTransfersByUserId(
            @PathVariable("userId") @Valid Long userId,
            Pageable pageable
    ) {
        Page<TransferResponse> responses = transferService.getAllTransfersByUserId(userId, pageable);

        return ResponseEntity.status(200).body(responses);
    }

    @Operation(summary = "Get all transfers with pageable")
    @GetMapping
    public ResponseEntity<?> getAllTransfers(Pageable pageable) {
        Page<TransferResponse> responses = transferService.getAllTransfers(pageable);

        return ResponseEntity.status(200).body(responses);
    }

}
