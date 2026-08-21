package com.example.rewards.reward;

import com.example.rewards.common.config.CacheConfig;
import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import com.example.rewards.reward.engine.RewardCalculationResult;
import com.example.rewards.reward.engine.RewardEngine;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRuleRepository rewardRuleRepository;
    private final RewardEngine rewardEngine;
    private final WalletService walletService;
    private final ObjectMapper objectMapper;

    public List<Reward> getRewardsByUserId(Long userId) {
        return rewardRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public List<Reward> getRewardsByTransactionId(Long transactionId) {
        return rewardRepository.findByTransactionId(transactionId);
    }

    @Transactional
    public List<Reward> processTransactionRewards(Transaction transaction, User user) {
        RewardCalculationResult calculationResult = rewardEngine.calculateRewards(transaction, user);

        List<Reward> createdRewards = new ArrayList<>();
        String breakdownJson = "";
        try {
            breakdownJson = objectMapper.writeValueAsString(calculationResult.getBreakdowns());
        } catch (Exception e) {
            log.error("Failed to serialize breakdown json: {}", e.getMessage());
        }

        // 1. Process Cashback if any
        if (calculationResult.getTotalCashback().compareTo(BigDecimal.ZERO) > 0) {
            Reward cashbackReward = Reward.builder()
                    .userId(user.getId())
                    .transactionId(transaction.getId())
                    .type(RewardType.CASHBACK)
                    .amount(calculationResult.getTotalCashback())
                    .points(0L)
                    .status(RewardStatus.CREDITED)
                    .description("Cashback for " + transaction.getMerchantName() + " (" + transaction.getCategory() + ")")
                    .breakdownJson(breakdownJson)
                    .createdAt(LocalDateTime.now())
                    .build();

            Reward savedReward = rewardRepository.save(cashbackReward);
            walletService.creditCashback(user.getId(), calculationResult.getTotalCashback());
            createdRewards.add(savedReward);
        }

        // 2. Process Points if any
        if (calculationResult.getTotalPoints() > 0) {
            Reward pointsReward = Reward.builder()
                    .userId(user.getId())
                    .transactionId(transaction.getId())
                    .type(RewardType.POINTS)
                    .amount(BigDecimal.ZERO)
                    .points(calculationResult.getTotalPoints())
                    .status(RewardStatus.CREDITED)
                    .description("Points for " + transaction.getMerchantName() + " (" + transaction.getCategory() + ")")
                    .breakdownJson(breakdownJson)
                    .expiresAt(LocalDateTime.now().plusYears(1))
                    .createdAt(LocalDateTime.now())
                    .build();

            Reward savedReward = rewardRepository.save(pointsReward);
            walletService.creditPoints(user.getId(), calculationResult.getTotalPoints());
            createdRewards.add(savedReward);
        }

        return createdRewards;
    }

    @Transactional
    public List<Reward> reverseRewardsForTransaction(Transaction transaction) {
        List<Reward> originalRewards = rewardRepository.findByTransactionId(transaction.getId());
        List<Reward> reversals = new ArrayList<>();

        for (Reward orig : originalRewards) {
            if (orig.getStatus() == RewardStatus.CREDITED) {
                // Create an explicit REVERSED reward audit entry rather than mutating or deleting original
                Reward reversal = Reward.builder()
                        .userId(orig.getUserId())
                        .transactionId(orig.getTransactionId())
                        .type(orig.getType())
                        .amount(orig.getAmount().negate()) // Negative amount to represent reversal in ledger
                        .points(-orig.getPoints())
                        .status(RewardStatus.REVERSED)
                        .description("Reversal of Reward #" + orig.getId() + " due to transaction refund")
                        .breakdownJson(orig.getBreakdownJson())
                        .createdAt(LocalDateTime.now())
                        .build();

                Reward savedReversal = rewardRepository.save(reversal);
                reversals.add(savedReversal);

                // Update original status to indicate it has been reversed
                orig.setStatus(RewardStatus.REVERSED);
                rewardRepository.save(orig);

                // Deduct from wallet
                if (orig.getType() == RewardType.CASHBACK && orig.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    walletService.reverseCashback(orig.getUserId(), orig.getAmount());
                } else if (orig.getType() == RewardType.POINTS && orig.getPoints() > 0) {
                    walletService.reversePoints(orig.getUserId(), orig.getPoints());
                }
            }
        }
        return reversals;
    }

    // Rules CRUD for Admin
    @Cacheable(value = CacheConfig.ACTIVE_RULES_CACHE)
    public List<RewardRuleEntity> getAllRules() {
        return rewardRuleRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_RULES_CACHE, allEntries = true)
    public RewardRuleEntity createRule(RewardRuleEntity rule) {
        return rewardRuleRepository.save(rule);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_RULES_CACHE, allEntries = true)
    public RewardRuleEntity toggleRule(Long ruleId) {
        RewardRuleEntity rule = rewardRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + ruleId));
        rule.setActive(!rule.isActive());
        return rewardRuleRepository.save(rule);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_RULES_CACHE, allEntries = true)
    public void deleteRule(Long ruleId) {
        if (!rewardRuleRepository.existsById(ruleId)) {
            throw new ResourceNotFoundException("Rule not found with id: " + ruleId);
        }
        rewardRuleRepository.deleteById(ruleId);
    }
}
