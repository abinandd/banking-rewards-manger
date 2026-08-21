package com.example.rewards.wallet;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public RewardWallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        RewardWallet.builder()
                                .userId(userId)
                                .cashbackBalance(BigDecimal.ZERO)
                                .pointsBalance(0L)
                                .build()
                ));
    }

    @Transactional
    public RewardWallet creditCashback(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWallet(userId);
        wallet.setCashbackBalance(wallet.getCashbackBalance().add(amount));
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet creditPoints(Long userId, Long points) {
        if (points <= 0) {
            return getOrCreateWallet(userId);
        }
        RewardWallet wallet = getOrCreateWallet(userId);
        wallet.setPointsBalance(wallet.getPointsBalance() + points);
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet reverseCashback(Long userId, BigDecimal amount) {
        RewardWallet wallet = getOrCreateWallet(userId);
        BigDecimal newBalance = wallet.getCashbackBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            // Allow zero floor or negative balance depending on business rule, here we set to zero if desired, or allow exact balance change with warning
            wallet.setCashbackBalance(BigDecimal.ZERO.max(newBalance));
        } else {
            wallet.setCashbackBalance(newBalance);
        }
        return walletRepository.save(wallet);
    }

    @Transactional
    public RewardWallet reversePoints(Long userId, Long points) {
        RewardWallet wallet = getOrCreateWallet(userId);
        long newBalance = wallet.getPointsBalance() - points;
        wallet.setPointsBalance(Math.max(0L, newBalance));
        return walletRepository.save(wallet);
    }

    public RewardWallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }
}
