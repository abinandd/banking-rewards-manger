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
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Calculate rewards for Gold tier user on Groceries")
    void testGroceriesGoldTierCashbackCalculation() {
        User user = User.builder().id(1L).name("Abhinand").tier(UserTier.GOLD).build();

        Transaction transaction = Transaction.builder()
                .id(101L)
                .userId(1L)
                .merchantName("XYZ Supermarket")
                .category("GROCERIES")
                .amount(new BigDecimal("5000.00"))
                .build();

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

        // Base 1% (₹50) + Category 1% (₹50) + Gold Tier 0.5% (₹25) = ₹125.00
        assertEquals(new BigDecimal("125.00"), result.getTotalCashback());
        assertTrue(result.getBreakdowns().size() >= 3);
    }

    @Test
    @DisplayName("Calculate rewards for Platinum tier user with campaign booster")
    void testPlatinumUserWithCampaign() {
        User user = User.builder().id(2L).name("Priya").tier(UserTier.PLATINUM).build();

        Transaction transaction = Transaction.builder()
                .id(102L)
                .userId(2L)
                .merchantName("Amazon")
                .category("ELECTRONICS")
                .amount(new BigDecimal("10000.00"))
                .build();

        Campaign techCampaign = Campaign.builder()
                .name("Tech Carnival")
                .category("ELECTRONICS")
                .merchant("Amazon")
                .bonusPercentage(new BigDecimal("3.00"))
                .minTransactionAmount(new BigDecimal("1000.00"))
                .maxReward(new BigDecimal("500.00"))
                .bonusPoints(250L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .active(true)
                .build();

        when(rewardRuleRepository.findByActiveTrue()).thenReturn(List.of());
        when(campaignService.getActiveCampaigns()).thenReturn(List.of(techCampaign));

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Base: 1% of 10000 = 100.00
        // Platinum tier: 1.0% = 100.00, points = 2000 (3x multiplier = 2x bonus)
        // Campaign: 3% of 10000 = 300.00 (under 500 cap), bonusPoints = 250
        // Total Cashback: 100 + 100 + 300 = 500.00
        // Total Points: (Base 1pt per ₹10 = 1000) + Tier 2000 + Campaign 250 = 3250
        assertEquals(new BigDecimal("500.00"), result.getTotalCashback());
        assertEquals(3250L, result.getTotalPoints());
    }

    @Test
    @DisplayName("Respect max reward cap on category rule")
    void testMaxRewardCapOnRule() {
        User user = User.builder().id(3L).name("Rahul").tier(UserTier.SILVER).build();

        Transaction transaction = Transaction.builder()
                .id(103L)
                .userId(3L)
                .merchantName("Luxury Store")
                .category("SHOPPING")
                .amount(new BigDecimal("100000.00"))
                .build();

        RewardRuleEntity cappedRule = RewardRuleEntity.builder()
                .category("SHOPPING")
                .merchant("ALL")
                .rewardType(RewardType.CASHBACK)
                .rewardValue(new BigDecimal("5.00")) // 5% would be 5000, but cap is 500
                .maxReward(new BigDecimal("500.00"))
                .active(true)
                .build();

        when(rewardRuleRepository.findByActiveTrue()).thenReturn(List.of(cappedRule));
        when(campaignService.getActiveCampaigns()).thenReturn(List.of());

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Base: 1% of 100,000 = 1,000.00
        // Capped rule: min(5000, 500) = 500.00
        // Silver tier: 0% extra
        // Total Cashback = 1500.00
        assertEquals(new BigDecimal("1500.00"), result.getTotalCashback());
    }
}
