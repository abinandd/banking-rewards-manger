package com.example.rewards.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTransactions_ReturnsSeedTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void createTransaction_ValidRequest_CreatesTransactionAndSettlesRewards() throws Exception {
        TransactionController.CreateTransactionRequest request = new TransactionController.CreateTransactionRequest();
        request.setUserId(1L);
        request.setMerchantName("Amazon");
        request.setCategory("ELECTRONICS");
        request.setAmount(new BigDecimal("1500.00"));
        request.setDescription("Integration Test Purchase");

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.amount").value(1500.00));
    }

    @Test
    void createTransaction_InvalidAmount_ReturnsBadRequest() throws Exception {
        TransactionController.CreateTransactionRequest request = new TransactionController.CreateTransactionRequest();
        request.setUserId(1L);
        request.setMerchantName("Amazon");
        request.setCategory("ELECTRONICS");
        request.setAmount(new BigDecimal("0.00")); // Violates @DecimalMin("0.01")

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.amount").exists());
    }

    @Test
    void refundTransaction_ValidId_ReturnsRefundedStatus() throws Exception {
        // Create first
        TransactionController.CreateTransactionRequest request = new TransactionController.CreateTransactionRequest();
        request.setUserId(1L);
        request.setMerchantName("Test Merchant");
        request.setCategory("DINING");
        request.setAmount(new BigDecimal("500.00"));

        String response = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Transaction created = objectMapper.readValue(response, Transaction.class);

        // Refund it
        mockMvc.perform(post("/api/transactions/" + created.getId() + "/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        // Refunding again should fail
        mockMvc.perform(post("/api/transactions/" + created.getId() + "/refund"))
                .andExpect(status().isBadRequest());
    }
}
