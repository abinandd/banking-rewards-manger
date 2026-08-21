package com.example.rewards.transaction;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import com.example.rewards.reward.Reward;
import com.example.rewards.reward.RewardService;
import com.example.rewards.user.User;
import com.example.rewards.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final RewardService rewardService;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Transactional
    public Transaction createTransaction(Long userId, String merchantName, String category, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transaction amount must be strictly greater than 0");
        }

        User user = userService.getUserById(userId);

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .merchantName(merchantName)
                .category(category.toUpperCase())
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .build();

        Transaction savedTxn = transactionRepository.save(transaction);
        log.info("Saved transaction #{} for user {}", savedTxn.getId(), user.getName());

        // Process Rewards immediately via Reward Engine
        rewardService.processTransactionRewards(savedTxn, user);

        return savedTxn;
    }

    @Transactional
    public Transaction refundTransaction(Long id) {
        Transaction transaction = getTransactionById(id);

        if (transaction.getStatus() == TransactionStatus.REFUNDED) {
            throw new BusinessException("Transaction is already refunded");
        }

        transaction.setStatus(TransactionStatus.REFUNDED);
        Transaction saved = transactionRepository.save(transaction);
        log.info("Refunding transaction #{}", saved.getId());

        // Reverse rewards
        rewardService.reverseRewardsForTransaction(saved);

        return saved;
    }
}
