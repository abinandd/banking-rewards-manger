package com.example.rewards.reward.engine;

import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import com.example.rewards.user.UserTier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Order(4)
public class TierRewardRule implements RewardRule {

    @Override
    public String getRuleName() {
        return "Membership Tier Bonus";
    }

    @Override
    public void evaluate(Transaction transaction, User user, RewardCalculationResult result) {
        UserTier tier = user.getTier() != null ? user.getTier() : UserTier.SILVER;

        BigDecimal tierBonusPercent = BigDecimal.ZERO;
        long tierMultiplier = 1;

        if (tier == UserTier.GOLD) {
            tierBonusPercent = new BigDecimal("0.5"); // +0.5%
            tierMultiplier = 2; // 2x points
        } else if (tier == UserTier.PLATINUM) {
            tierBonusPercent = new BigDecimal("1.0"); // +1.0%
            tierMultiplier = 3; // 3x points
        }

        if (tierBonusPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cashback = transaction.getAmount()
                    .multiply(tierBonusPercent)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            result.addCashback(getRuleName() + " (" + tier.name() + ")",
                    tier.name() + " tier loyalty booster of +" + tierBonusPercent + "%",
                    tierBonusPercent, cashback);
        }

        if (tierMultiplier > 1) {
            long bonusPoints = (transaction.getAmount().longValue() / 10) * (tierMultiplier - 1);
            if (bonusPoints > 0) {
                result.addPoints(getRuleName() + " Points (" + tier.name() + ")",
                        tier.name() + " tier " + tierMultiplier + "x points boost",
                        bonusPoints);
            }
        }
    }
}
