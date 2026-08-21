package com.example.rewards.reward.engine;

import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardEngine {

    private final List<RewardRule> rules;

    public RewardCalculationResult calculateRewards(Transaction transaction, User user) {
        log.info("Calculating rewards for transaction id: {} (User: {}, Amount: {}, Category: {}, Merchant: {})",
                transaction.getId(), user.getName(), transaction.getAmount(), transaction.getCategory(), transaction.getMerchantName());

        RewardCalculationResult result = new RewardCalculationResult();

        for (RewardRule rule : rules) {
            try {
                rule.evaluate(transaction, user, result);
            } catch (Exception e) {
                log.error("Error evaluating reward rule {}: {}", rule.getRuleName(), e.getMessage(), e);
            }
        }

        log.info("Finished reward calculation. Total Cashback: ₹{}, Total Points: {}",
                result.getTotalCashback(), result.getTotalPoints());

        return result;
    }
}
