package com.example.rewards.reward.engine;

import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Order(1)
public class BaseRewardRule implements RewardRule {

    private static final BigDecimal BASE_CASHBACK_PERCENT = new BigDecimal("1.0"); // 1.0%

    @Override
    public String getRuleName() {
        return "Base Cashback";
    }

    @Override
    public void evaluate(Transaction transaction, User user, RewardCalculationResult result) {
        // Base 1% cashback on every transaction
        BigDecimal cashback = transaction.getAmount()
                .multiply(BASE_CASHBACK_PERCENT)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        result.addCashback(getRuleName(), "Standard 1% base reward on all transactions", BASE_CASHBACK_PERCENT, cashback);

        // Also base 1 point per 10 INR spent
        long points = transaction.getAmount().divide(BigDecimal.valueOf(10), 0, RoundingMode.DOWN).longValue();
        if (points > 0) {
            result.addPoints("Base Reward Points", "1 Point per ₹10 spent", points);
        }
    }
}
