package com.example.rewards.transaction;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import com.example.rewards.reward.RewardService;
import com.example.rewards.user.User;
import com.example.rewards.user.UserService;
import com.example.rewards.user.UserTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private TransactionService transactionService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Abhinand")
                .email("abhinand@rewardsbank.com")
                .tier(UserTier.GOLD)
                .build();
    }

    @Test
    void createTransaction_ValidAmount_Success() {
        when(userService.getUserById(1L)).thenReturn(sampleUser);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction txn = invocation.getArgument(0);
            txn.setId(99L);
            return txn;
        });

        Transaction txn = transactionService.createTransaction(
                1L, "Amazon", "ELECTRONICS", new BigDecimal("1500.00"), "Headphones purchase"
        );

        assertThat(txn).isNotNull();
        assertThat(txn.getId()).isEqualTo(99L);
        assertThat(txn.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(txn.getCategory()).isEqualTo("ELECTRONICS");

        verify(rewardService).processTransactionRewards(txn, sampleUser);
    }

    @Test
    void createTransaction_ZeroOrNegativeAmount_ThrowsBusinessException() {
        assertThatThrownBy(() -> transactionService.createTransaction(
                1L, "Amazon", "ELECTRONICS", BigDecimal.ZERO, "Invalid amount"
        )).isInstanceOf(BusinessException.class)
          .hasMessageContaining("strictly greater than 0");

        assertThatThrownBy(() -> transactionService.createTransaction(
                1L, "Amazon", "ELECTRONICS", new BigDecimal("-50.00"), "Negative amount"
        )).isInstanceOf(BusinessException.class);

        verifyNoInteractions(userService);
        verifyNoInteractions(rewardService);
    }

    @Test
    void refundTransaction_ValidCompletedTransaction_Success() {
        Transaction completedTxn = Transaction.builder()
                .id(10L)
                .userId(1L)
                .merchantName("Amazon")
                .category("ELECTRONICS")
                .amount(new BigDecimal("1000.00"))
                .status(TransactionStatus.COMPLETED)
                .build();

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(completedTxn));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction refunded = transactionService.refundTransaction(10L);

        assertThat(refunded.getStatus()).isEqualTo(TransactionStatus.REFUNDED);
        verify(rewardService).reverseRewardsForTransaction(refunded);
    }

    @Test
    void refundTransaction_AlreadyRefunded_ThrowsBusinessException() {
        Transaction refundedTxn = Transaction.builder()
                .id(11L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .status(TransactionStatus.REFUNDED)
                .build();

        when(transactionRepository.findById(11L)).thenReturn(Optional.of(refundedTxn));

        assertThatThrownBy(() -> transactionService.refundTransaction(11L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Transaction is already refunded");

        verify(rewardService, never()).reverseRewardsForTransaction(any());
    }

    @Test
    void refundTransaction_NotFound_ThrowsResourceNotFoundException() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.refundTransaction(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
