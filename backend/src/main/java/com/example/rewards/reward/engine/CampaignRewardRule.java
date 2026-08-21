package com.example.rewards.reward.engine;

import com.example.rewards.campaign.Campaign;
import com.example.rewards.campaign.CampaignService;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Order(5)
@RequiredArgsConstructor
public class CampaignRewardRule implements RewardRule {

    private final CampaignService campaignService;

    @Override
    public String getRuleName() {
        return "Special Campaign Offer";
    }

    @Override
    public void evaluate(Transaction transaction, User user, RewardCalculationResult result) {
        List<Campaign> activeCampaigns = campaignService.getActiveCampaigns();

        for (Campaign c : activeCampaigns) {
            boolean categoryMatch = c.getCategory() == null ||
                    c.getCategory().equalsIgnoreCase("ALL") ||
                    (transaction.getCategory() != null && c.getCategory().equalsIgnoreCase(transaction.getCategory()));

            boolean merchantMatch = c.getMerchant() == null ||
                    c.getMerchant().equalsIgnoreCase("ALL") ||
                    (transaction.getMerchantName() != null && c.getMerchant().equalsIgnoreCase(transaction.getMerchantName()));

            if (categoryMatch && merchantMatch) {
                if (c.getMinTransactionAmount() != null &&
                        transaction.getAmount().compareTo(c.getMinTransactionAmount()) < 0) {
                    continue;
                }

                if (c.getBonusPercentage() != null && c.getBonusPercentage().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal rate = c.getBonusPercentage();
                    BigDecimal cashback = transaction.getAmount()
                            .multiply(rate)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    if (c.getMaxReward() != null && cashback.compareTo(c.getMaxReward()) > 0) {
                        cashback = c.getMaxReward();
                    }

                    result.addCashback(getRuleName() + ": " + c.getName(),
                            c.getDescription() != null ? c.getDescription() : "Special Campaign Bonus",
                            rate, cashback);
                }

                if (c.getBonusPoints() != null && c.getBonusPoints() > 0) {
                    result.addPoints(getRuleName() + " Points: " + c.getName(),
                            "Campaign flat bonus points",
                            c.getBonusPoints());
                }
            }
        }
    }
}
