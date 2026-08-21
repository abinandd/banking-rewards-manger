package com.example.rewards;

import com.example.rewards.campaign.Campaign;
import com.example.rewards.campaign.CampaignService;
import com.example.rewards.reward.RewardRuleEntity;
import com.example.rewards.reward.RewardRuleRepository;
import com.example.rewards.reward.RewardType;
import com.example.rewards.reward.engine.*;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import com.example.rewards.user.UserTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RewardEngineTest {

    private RewardEngine rewardEngine;
    private RewardRuleRepository rewardRuleRepository;
    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        rewardRuleRepository = Mockito.mock(RewardRuleRepository.class);
        campaignService = Mockito.mock(CampaignService.class);

        BaseRewardRule baseRewardRule = new BaseRewardRule();
        CategoryRewardRule categoryRewardRule = new CategoryRewardRule(rewardRuleRepository);
        MerchantRewardRule merchantRewardRule = new MerchantRewardRule(rewardRuleRepository);
        TierRewardRule tierRewardRule = new TierRewardRule();
        CampaignRewardRule campaignRewardRule = new CampaignRewardRule(campaignService);

        rewardEngine = new RewardEngine(List.of(
                baseRewardRule,
                categoryRewardRule,
                merchantRewardRule,
                tierRewardRule,
                campaignRewardRule
        ));
    }

    @Test
    void testGroceriesGoldTierCashbackCalculation() {
        // User: Abhinand (GOLD tier)
        User user = User.builder().id(1L).name("Abhinand").tier(UserTier.GOLD).build();

        // Transaction: XYZ Supermarket, GROCERIES, ₹5,000
        Transaction transaction = Transaction.builder()
                .id(101L)
                .userId(1L)
                .merchantName("XYZ Supermarket")
                .category("GROCERIES")
                .amount(new BigDecimal("5000.00"))
                .build();

        // Groceries rule: 1%
        RewardRuleEntity groceriesRule = RewardRuleEntity.builder()
                .category("GROCERIES")
                .merchant("ALL")
                .rewardType(RewardType.CASHBACK)
                .rewardValue(new BigDecimal("1.00"))
                .active(true)
                .build();

        when(rewardRuleRepository.findByActiveTrue()).thenReturn(List.of(groceriesRule));
        when(campaignService.getActiveCampaigns()).thenReturn(List.of());

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Expected:
        // Base 1% = ₹50
        // Groceries 1% = ₹50
        // Gold Tier 0.5% = ₹25
        // Total = ₹125.00
        assertEquals(new BigDecimal("125.00"), result.getTotalCashback());
        assertTrue(result.getBreakdowns().size() >= 3);
    }
}
