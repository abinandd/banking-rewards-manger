package com.example.rewards.wallet;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public RewardWallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    try {
                        return walletRepository.save(
                                RewardWallet.builder()
                                        .userId(userId)
                                        .cashbackBalance(BigDecimal.ZERO)
                                        .pointsBalance(0L)
                                        .build()
                        );
                    } catch (Exception e) {
                        // Fallback if created concurrently
                        return walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new BusinessException("Could not initialize wallet for user id: " + userId));
                    }
                });
    }

    @Transactional
    public RewardWallet getOrCreateWalletWithLock(Long userId) {
        return walletRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> getOrCreateWallet(userId));
    }

    @Transactional
    public RewardWallet creditCashback(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWalletWithLock(userId);
        wallet.setCashbackBalance(wallet.getCashbackBalance().add(amount));
        log.info("Credited ₹{} cashback to user #{} wallet. New Balance: ₹{}", amount, userId, wallet.getCashbackBalance());
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet creditPoints(Long userId, Long points) {
        if (points == null || points <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWalletWithLock(userId);
        wallet.setPointsBalance(wallet.getPointsBalance() + points);
        log.info("Credited {} points to user #{} wallet. New Points Balance: {}", points, userId, wallet.getPointsBalance());
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet reverseCashback(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWalletWithLock(userId);
        BigDecimal newBalance = wallet.getCashbackBalance().subtract(amount);
        wallet.setCashbackBalance(BigDecimal.ZERO.max(newBalance));
        log.info("Reversed ₹{} cashback from user #{} wallet. New Balance: ₹{}", amount, userId, wallet.getCashbackBalance());
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet reversePoints(Long userId, Long points) {
        if (points == null || points <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWalletWithLock(userId);
        long newBalance = wallet.getPointsBalance() - points;
        wallet.setPointsBalance(Math.max(0L, newBalance));
        log.info("Reversed {} points from user #{} wallet. New Points Balance: {}", points, userId, wallet.getPointsBalance());
        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public RewardWallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }
}
