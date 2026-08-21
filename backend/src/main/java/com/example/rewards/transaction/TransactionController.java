package com.example.rewards.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Data
    public static class CreateTransactionRequest {
        @NotNull(message = "userId is required")
        private Long userId;

        @NotBlank(message = "merchantName is required")
        private String merchantName;

        @NotBlank(message = "category is required")
        private String category;

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        private BigDecimal amount;

        private String description;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
        }
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction txn = transactionService.createTransaction(
                request.getUserId(),
                request.getMerchantName(),
                request.getCategory(),
                request.getAmount(),
                request.getDescription()
        );
        return new ResponseEntity<>(txn, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Transaction> refundTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.refundTransaction(id));
    }
}
