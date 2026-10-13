package com.example.rewards.reward.engine;

import com.example.rewards.campaign.Campaign;
import com.example.rewards.campaign.CampaignService;
import com.example.rewards.reward.ActiveRuleProvider;
import com.example.rewards.reward.RewardRuleEntity;
import com.example.rewards.reward.RewardType;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;
import com.example.rewards.user.UserTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardEngineTest {

    @Mock
    private ActiveRuleProvider activeRuleProvider;

    @Mock
    private CampaignService campaignService;

    private RewardEngine rewardEngine;

    @BeforeEach
    void setUp() {
        BaseRewardRule baseRule = new BaseRewardRule();
        CategoryRewardRule categoryRule = new CategoryRewardRule(activeRuleProvider);
        MerchantRewardRule merchantRule = new MerchantRewardRule(activeRuleProvider);
        TierRewardRule tierRule = new TierRewardRule();
        CampaignRewardRule campaignRule = new CampaignRewardRule(campaignService);

        rewardEngine = new RewardEngine(List.of(baseRule, categoryRule, merchantRule, tierRule, campaignRule));
    }

    @Test
    void calculateRewards_SilverTier_BaseRuleOnly() {
        when(activeRuleProvider.getActiveRules()).thenReturn(List.of());
        when(campaignService.getActiveCampaigns()).thenReturn(List.of());

        User user = User.builder().id(1L).name("Test User").tier(UserTier.SILVER).build();
        Transaction transaction = Transaction.builder()
                .id(100L)
                .userId(1L)
                .merchantName("Unknown Store")
                .category("OTHER")
                .amount(new BigDecimal("1000.00"))
                .build();

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // 1% cashback on 1000 = 10.00
        assertThat(result.getTotalCashback()).isEqualByComparingTo("10.00");
        // 1 point per 10 INR = 100 points
        assertThat(result.getTotalPoints()).isEqualTo(100L);
        assertThat(result.getBreakdowns()).hasSize(2);
    }

    @Test
    void calculateRewards_GoldTier_WithCategoryAndMerchantRules() {
        RewardRuleEntity groceryRule = RewardRuleEntity.builder()
                .id(1L)
                .category("GROCERIES")
                .merchant("ALL")
                .rewardType(RewardType.CASHBACK)
                .rewardValue(new BigDecimal("2.00"))
                .maxReward(new BigDecimal("200.00"))
                .active(true)
                .build();

        RewardRuleEntity amazonRule = RewardRuleEntity.builder()
                .id(2L)
                .category("ALL")
                .merchant("Amazon")
                .rewardType(RewardType.CASHBACK)
                .rewardValue(new BigDecimal("1.50"))
                .maxReward(new BigDecimal("100.00"))
                .active(true)
                .build();

        when(activeRuleProvider.getActiveRules()).thenReturn(List.of(groceryRule, amazonRule));
        when(campaignService.getActiveCampaigns()).thenReturn(List.of());

        User user = User.builder().id(2L).name("Gold User").tier(UserTier.GOLD).build();
        Transaction transaction = Transaction.builder()
                .id(101L)
                .userId(2L)
                .merchantName("Amazon")
                .category("GROCERIES")
                .amount(new BigDecimal("2000.00"))
                .build();

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Base 1% = 20.00
        // Category GROCERIES 2% = 40.00
        // Merchant Amazon 1.5% = 30.00
        // Gold Tier +0.5% = 10.00
        // Total Cashback = 100.00
        assertThat(result.getTotalCashback()).isEqualByComparingTo("100.00");

        // Base points: 2000 / 10 = 200 points
        // Gold tier 2x points boost: additional 200 points
        // Total Points = 400
        assertThat(result.getTotalPoints()).isEqualTo(400L);
    }

    @Test
    void calculateRewards_PlatinumTier_WithActiveCampaign() {
        Campaign campaign = Campaign.builder()
                .id(10L)
                .name("Diwali Bonanza")
                .category("ELECTRONICS")
                .merchant("ALL")
                .bonusPercentage(new BigDecimal("3.00"))
                .bonusPoints(250L)
                .active(true)
                .build();

        when(activeRuleProvider.getActiveRules()).thenReturn(List.of());
        when(campaignService.getActiveCampaigns()).thenReturn(List.of(campaign));

        User user = User.builder().id(3L).name("Platinum User").tier(UserTier.PLATINUM).build();
        Transaction transaction = Transaction.builder()
                .id(102L)
                .userId(3L)
                .merchantName("Croma")
                .category("ELECTRONICS")
                .amount(new BigDecimal("5000.00"))
                .build();

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Base 1% = 50.00
        // Platinum tier +1.0% = 50.00
        // Campaign +3.0% = 150.00
        // Total Cashback = 250.00
        assertThat(result.getTotalCashback()).isEqualByComparingTo("250.00");

        // Base points: 5000 / 10 = 500
        // Platinum tier 3x boost: additional (3-1) * 500 = 1000
        // Campaign bonus points: 250
        // Total Points = 1750
        assertThat(result.getTotalPoints()).isEqualTo(1750L);
    }

    @Test
    void calculateRewards_CategoryRule_MaxRewardCapApplied() {
        RewardRuleEntity cappedRule = RewardRuleEntity.builder()
                .id(3L)
                .category("TRAVEL")
                .merchant("ALL")
                .rewardType(RewardType.CASHBACK)
                .rewardValue(new BigDecimal("5.00"))
                .maxReward(new BigDecimal("100.00")) // Cap is 100
                .active(true)
                .build();

        when(activeRuleProvider.getActiveRules()).thenReturn(List.of(cappedRule));
        when(campaignService.getActiveCampaigns()).thenReturn(List.of());

        User user = User.builder().id(4L).name("User").tier(UserTier.SILVER).build();
        Transaction transaction = Transaction.builder()
                .id(103L)
                .userId(4L)
                .merchantName("Airline")
                .category("TRAVEL")
                .amount(new BigDecimal("10000.00")) // 5% of 10000 would be 500, but capped at 100
                .build();

        RewardCalculationResult result = rewardEngine.calculateRewards(transaction, user);

        // Base 1% = 100.00
        // Category 5% (capped at 100) = 100.00
        // Total Cashback = 200.00
        assertThat(result.getTotalCashback()).isEqualByComparingTo("200.00");
    }
}
