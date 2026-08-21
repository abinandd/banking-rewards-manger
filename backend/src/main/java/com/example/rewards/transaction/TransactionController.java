package com.example.rewards.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Transactions", description = "Transaction management and reward settlement operations")
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

    @Operation(summary = "Get all transactions", description = "Retrieve all transactions or filter by user ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
        }
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Operation(summary = "Get transaction by ID", description = "Fetch a single transaction record by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @Operation(summary = "Create transaction", description = "Process a new banking purchase and calculate real-time rewards & cashback")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction processed and rewards credited successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or business exception")
    })
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

    @Operation(summary = "Refund transaction", description = "Refund a transaction and post an immutable reversed ledger audit entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction refunded and rewards reversed"),
            @ApiResponse(responseCode = "400", description = "Transaction is already refunded"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PostMapping("/{id}/refund")
    public ResponseEntity<Transaction> refundTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.refundTransaction(id));
    }
}
