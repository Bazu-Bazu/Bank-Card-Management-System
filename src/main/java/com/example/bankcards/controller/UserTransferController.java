package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateTransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.security.userDetails.CustomUserDetails;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/transfers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserTransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<?> createTransfer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateTransferRequest request
    ) {
        TransferResponse response = transferService.createTransfer(userDetails.getUserId(), request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getMyTransfers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        Page<TransferResponse> responses = transferService.getAllTransfersByUserId(userDetails.getUserId(), pageable);

        return ResponseEntity.status(200).body(responses);
    }

}
