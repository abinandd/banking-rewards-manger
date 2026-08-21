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
@Order(3)
@RequiredArgsConstructor
public class MerchantRewardRule implements RewardRule {

    private final RewardRuleRepository rewardRuleRepository;

    @Override
    public String getRuleName() {
        return "Merchant Partner Bonus";
    }

    @Override
    public void evaluate(Transaction transaction, User user, RewardCalculationResult result) {
        String merchant = transaction.getMerchantName();
        if (merchant == null) return;

        List<RewardRuleEntity> activeRules = rewardRuleRepository.findByActiveTrue();

        for (RewardRuleEntity rule : activeRules) {
            if (rule.getMerchant() != null && !rule.getMerchant().equalsIgnoreCase("ALL")
                    && rule.getMerchant().trim().equalsIgnoreCase(merchant.trim())) {

                if (rule.getMinSpend() != null && transaction.getAmount().compareTo(rule.getMinSpend()) < 0) {
                    continue;
                }

                if (rule.getRewardType() == RewardType.CASHBACK) {
                    BigDecimal rate = rule.getRewardValue();
                    BigDecimal amount = transaction.getAmount().multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    if (rule.getMaxReward() != null && amount.compareTo(rule.getMaxReward()) > 0) {
                        amount = rule.getMaxReward();
                    }
                    result.addCashback(getRuleName() + " (" + merchant + ")",
                            merchant + " partner bonus of " + rate + "%", rate, amount);
                } else if (rule.getRewardType() == RewardType.POINTS) {
                    long pts = transaction.getAmount().multiply(rule.getRewardValue()).longValue();
                    result.addPoints(getRuleName() + " Points (" + merchant + ")",
                            merchant + " bonus points multiplier", pts);
                }
            }
        }
    }
}
