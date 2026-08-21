package com.example.rewards.reward.engine;

import com.example.rewards.reward.RewardRuleEntity;
import com.example.rewards.reward.RewardRuleRepository;
import com.example.rewards.reward.RewardType;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class CategoryRewardRule implements RewardRule {

    private final RewardRuleRepository rewardRuleRepository;

    @Override
    public String getRuleName() {
        return "Category Bonus";
    }

    @Override
    public void evaluate(Transaction transaction, User user, RewardCalculationResult result) {
        String category = transaction.getCategory();
        if (category == null) return;

        List<RewardRuleEntity> activeRules = rewardRuleRepository.findByActiveTrue();

        for (RewardRuleEntity rule : activeRules) {
            boolean categoryMatch = rule.getCategory() != null &&
                    (rule.getCategory().equalsIgnoreCase("ALL") || rule.getCategory().equalsIgnoreCase(category));
            boolean isCategoryOnly = rule.getMerchant() == null || rule.getMerchant().equalsIgnoreCase("ALL");

            if (categoryMatch && isCategoryOnly) {
                if (rule.getMinSpend() != null && transaction.getAmount().compareTo(rule.getMinSpend()) < 0) {
                    continue;
                }

                if (rule.getRewardType() == RewardType.CASHBACK) {
                    BigDecimal rate = rule.getRewardValue();
                    BigDecimal amount = transaction.getAmount().multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    if (rule.getMaxReward() != null && amount.compareTo(rule.getMaxReward()) > 0) {
                        amount = rule.getMaxReward();
                    }
                    result.addCashback(getRuleName() + " (" + category + ")",
                            category + " category bonus of " + rate + "%", rate, amount);
                } else if (rule.getRewardType() == RewardType.POINTS) {
                    long pts = transaction.getAmount().multiply(rule.getRewardValue()).longValue();
                    result.addPoints(getRuleName() + " Points (" + category + ")",
                            category + " bonus multiplier", pts);
                }
            }
        }
    }
}
