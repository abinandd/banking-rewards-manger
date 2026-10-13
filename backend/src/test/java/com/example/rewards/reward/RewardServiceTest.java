package com.example.rewards.reward;

import com.example.rewards.reward.engine.RewardCalculationResult;
import com.example.rewards.reward.engine.RewardEngine;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import com.example.rewards.user.UserTier;
import com.example.rewards.wallet.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private RewardRuleRepository rewardRuleRepository;

    @Mock
    private RewardEngine rewardEngine;

    @Mock
    private WalletService walletService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RewardService rewardService;

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Abhinand").tier(UserTier.GOLD).build();
        transaction = Transaction.builder()
                .id(100L)
                .userId(1L)
                .merchantName("Amazon")
                .category("ELECTRONICS")
                .amount(new BigDecimal("2000.00"))
                .build();
    }

    @Test
    void processTransactionRewards_CreatesCashbackAndPoints() {
        RewardCalculationResult calcResult = new RewardCalculationResult();
        calcResult.addCashback("Base", "Base 1%", new BigDecimal("1.0"), new BigDecimal("20.00"));
        calcResult.addPoints("Base Points", "1 pt per 10", 200L);

        when(rewardEngine.calculateRewards(transaction, user)).thenReturn(calcResult);
        when(rewardRepository.save(any(Reward.class))).thenAnswer(i -> {
            Reward r = i.getArgument(0);
            r.setId(10L);
            return r;
        });

        List<Reward> created = rewardService.processTransactionRewards(transaction, user);

        assertThat(created).hasSize(2);
        verify(walletService).creditCashback(eq(1L), eq(new BigDecimal("20.00")));
        verify(walletService).creditPoints(eq(1L), eq(200L));
    }

    @Test
    void reverseRewardsForTransaction_CreatesExplicitReversalEntriesAndAdjustsWallet() {
        Reward origCashback = Reward.builder()
                .id(1L)
                .userId(1L)
                .transactionId(100L)
                .type(RewardType.CASHBACK)
                .amount(new BigDecimal("50.00"))
                .points(0L)
                .status(RewardStatus.CREDITED)
                .build();

        Reward origPoints = Reward.builder()
                .id(2L)
                .userId(1L)
                .transactionId(100L)
                .type(RewardType.POINTS)
                .amount(BigDecimal.ZERO)
                .points(150L)
                .status(RewardStatus.CREDITED)
                .build();

        when(rewardRepository.findByTransactionId(100L)).thenReturn(List.of(origCashback, origPoints));
        when(rewardRepository.save(any(Reward.class))).thenAnswer(i -> i.getArgument(0));

        List<Reward> reversals = rewardService.reverseRewardsForTransaction(transaction);

        assertThat(reversals).hasSize(2);

        // Verify cashback reversal record
        Reward cashbackReversal = reversals.stream().filter(r -> r.getType() == RewardType.CASHBACK).findFirst().orElseThrow();
        assertThat(cashbackReversal.getStatus()).isEqualTo(RewardStatus.REVERSED);
        assertThat(cashbackReversal.getAmount()).isEqualByComparingTo("-50.00");

        // Verify points reversal record
        Reward pointsReversal = reversals.stream().filter(r -> r.getType() == RewardType.POINTS).findFirst().orElseThrow();
        assertThat(pointsReversal.getStatus()).isEqualTo(RewardStatus.REVERSED);
        assertThat(pointsReversal.getPoints()).isEqualTo(-150L);

        // Verify wallet debited
        verify(walletService).reverseCashback(1L, new BigDecimal("50.00"));
        verify(walletService).reversePoints(1L, 150L);
    }
}
