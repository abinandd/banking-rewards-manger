package com.example.rewards;

import com.example.rewards.transaction.Transaction;
import com.example.rewards.transaction.TransactionController;
import com.example.rewards.transaction.TransactionService;
import com.example.rewards.transaction.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    @DisplayName("POST /api/transactions with valid payload returns 201 and sets correlation ID")
    void testCreateTransactionValid() throws Exception {
        TransactionController.CreateTransactionRequest request = new TransactionController.CreateTransactionRequest();
        request.setUserId(1L);
        request.setMerchantName("Amazon");
        request.setCategory("ELECTRONICS");
        request.setAmount(new BigDecimal("1500.00"));
        request.setDescription("Purchase test");

        Transaction transaction = Transaction.builder()
                .id(999L)
                .userId(1L)
                .merchantName("Amazon")
                .category("ELECTRONICS")
                .amount(new BigDecimal("1500.00"))
                .status(TransactionStatus.COMPLETED)
                .build();

        when(transactionService.createTransaction(any(), any(), any(), any(), any())).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.amount").value(1500.00));
    }

    @Test
    @DisplayName("POST /api/transactions with invalid payload returns 400 with structured validation error")
    void testCreateTransactionValidationFailure() throws Exception {
        TransactionController.CreateTransactionRequest request = new TransactionController.CreateTransactionRequest();
        // Missing userId, merchantName, category, and non-positive amount
        request.setAmount(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.userId").exists())
                .andExpect(jsonPath("$.validationErrors.merchantName").exists())
                .andExpect(jsonPath("$.validationErrors.amount").exists());
    }
}
