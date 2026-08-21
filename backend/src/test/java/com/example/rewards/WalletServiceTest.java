package com.example.rewards;

import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletRepository;
import com.example.rewards.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Credit cashback increases balance correctly")
    void testCreditCashback() {
        RewardWallet existingWallet = RewardWallet.builder()
                .id(1L)
                .userId(100L)
                .cashbackBalance(new BigDecimal("50.00"))
                .pointsBalance(200L)
                .build();

        when(walletRepository.findByUserIdWithLock(100L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArguments()[0]);

        RewardWallet updated = walletService.creditCashback(100L, new BigDecimal("25.50"));

        assertEquals(new BigDecimal("75.50"), updated.getCashbackBalance());
        verify(walletRepository, times(1)).save(existingWallet);
    }

    @Test
    @DisplayName("Reverse cashback decreases balance and floors at zero")
    void testReverseCashbackFloorZero() {
        RewardWallet existingWallet = RewardWallet.builder()
                .id(1L)
                .userId(100L)
                .cashbackBalance(new BigDecimal("30.00"))
                .pointsBalance(100L)
                .build();

        when(walletRepository.findByUserIdWithLock(100L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArguments()[0]);

        // Attempt to reverse 50 when balance is only 30
        RewardWallet updated = walletService.reverseCashback(100L, new BigDecimal("50.00"));

        assertEquals(BigDecimal.ZERO, updated.getCashbackBalance());
    }

    @Test
    @DisplayName("Credit and reverse points work accurately")
    void testPointsCreditAndReverse() {
        RewardWallet existingWallet = RewardWallet.builder()
                .id(1L)
                .userId(100L)
                .cashbackBalance(BigDecimal.ZERO)
                .pointsBalance(150L)
                .build();

        when(walletRepository.findByUserIdWithLock(100L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(RewardWallet.class))).thenAnswer(i -> i.getArguments()[0]);

        walletService.creditPoints(100L, 50L);
        assertEquals(200L, existingWallet.getPointsBalance());

        walletService.reversePoints(100L, 75L);
        assertEquals(125L, existingWallet.getPointsBalance());
    }
}
