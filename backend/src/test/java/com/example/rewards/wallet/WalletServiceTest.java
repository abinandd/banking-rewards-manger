package com.example.rewards.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private RewardWallet wallet;

    @BeforeEach
    void setUp() {
        wallet = RewardWallet.builder()
                .id(1L)
                .userId(10L)
                .cashbackBalance(new BigDecimal("100.00"))
                .pointsBalance(500L)
                .build();
    }

    @Test
    void creditCashback_PositiveAmount_IncreasesBalance() {
        when(walletRepository.findByUserIdWithLock(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArgument(0));

        RewardWallet updated = walletService.creditCashback(10L, new BigDecimal("50.00"));

        assertThat(updated.getCashbackBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void creditCashback_NullOrZeroAmount_DoesNotModify() {
        when(walletRepository.findByUserId(10L)).thenReturn(Optional.of(wallet));

        RewardWallet updated = walletService.creditCashback(10L, BigDecimal.ZERO);

        assertThat(updated.getCashbackBalance()).isEqualByComparingTo("100.00");
        verify(walletRepository, never()).save(any());
    }

    @Test
    void reverseCashback_DeductsFromBalance() {
        when(walletRepository.findByUserIdWithLock(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArgument(0));

        RewardWallet updated = walletService.reverseCashback(10L, new BigDecimal("40.00"));

        assertThat(updated.getCashbackBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void reverseCashback_AmountExceedsBalance_FloorsAtZero() {
        when(walletRepository.findByUserIdWithLock(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArgument(0));

        RewardWallet updated = walletService.reverseCashback(10L, new BigDecimal("250.00"));

        assertThat(updated.getCashbackBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void creditPoints_IncreasesPointsBalance() {
        when(walletRepository.findByUserIdWithLock(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArgument(0));

        RewardWallet updated = walletService.creditPoints(10L, 200L);

        assertThat(updated.getPointsBalance()).isEqualTo(700L);
    }

    @Test
    void reversePoints_FloorsAtZero() {
        when(walletRepository.findByUserIdWithLock(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArgument(0));

        RewardWallet updated = walletService.reversePoints(10L, 1000L);

        assertThat(updated.getPointsBalance()).isEqualTo(0L);
    }
}
