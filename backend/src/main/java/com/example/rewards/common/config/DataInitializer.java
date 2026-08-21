package com.example.rewards.common.config;

import com.example.rewards.campaign.Campaign;
import com.example.rewards.campaign.CampaignRepository;
import com.example.rewards.referral.Referral;
import com.example.rewards.referral.ReferralRepository;
import com.example.rewards.referral.ReferralStatus;
import com.example.rewards.reward.RewardRuleEntity;
import com.example.rewards.reward.RewardRuleRepository;
import com.example.rewards.reward.RewardType;
import com.example.rewards.transaction.TransactionService;
import com.example.rewards.user.User;
import com.example.rewards.user.UserRepository;
import com.example.rewards.user.UserService;
import com.example.rewards.user.UserTier;
import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RewardRuleRepository rewardRuleRepository;
    private final CampaignRepository campaignRepository;
    private final ReferralRepository referralRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing Seed Data for Banking Rewards & Cashback Platform...");

        // 1. Seed Reward Rules
        if (rewardRuleRepository.count() == 0) {
            // Category Rules
            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("GROCERIES")
                    .merchant("ALL")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("1.00")) // 1% bonus
                    .maxReward(new BigDecimal("200.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("ELECTRONICS")
                    .merchant("ALL")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("1.00")) // 1% bonus
                    .maxReward(new BigDecimal("500.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("DINING")
                    .merchant("ALL")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("2.00")) // 2% bonus
                    .maxReward(new BigDecimal("150.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("TRAVEL")
                    .merchant("ALL")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("3.00")) // 3% bonus
                    .maxReward(new BigDecimal("1000.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("FUEL")
                    .merchant("ALL")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("0.50")) // 0.5% bonus
                    .maxReward(new BigDecimal("100.00"))
                    .active(true)
                    .build());

            // Merchant Partner Rules
            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("ALL")
                    .merchant("Amazon")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("1.50")) // 1.5% merchant booster
                    .maxReward(new BigDecimal("300.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("ALL")
                    .merchant("Swiggy")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("2.00")) // 2% merchant booster
                    .maxReward(new BigDecimal("100.00"))
                    .active(true)
                    .build());

            rewardRuleRepository.save(RewardRuleEntity.builder()
                    .category("ALL")
                    .merchant("MakeMyTrip")
                    .rewardType(RewardType.CASHBACK)
                    .rewardValue(new BigDecimal("3.00")) // 3% merchant booster
                    .maxReward(new BigDecimal("1500.00"))
                    .active(true)
                    .build());
        }

        // 2. Seed Active Campaigns
        if (campaignRepository.count() == 0) {
            campaignRepository.save(Campaign.builder()
                    .name("Mega Groceries Fiesta")
                    .description("Get an extra 2.5% cashback on all grocery spends this week")
                    .category("GROCERIES")
                    .merchant("ALL")
                    .bonusPercentage(new BigDecimal("2.50"))
                    .minTransactionAmount(new BigDecimal("500.00"))
                    .maxReward(new BigDecimal("250.00"))
                    .bonusPoints(100L)
                    .startDate(LocalDate.now().minusDays(5))
                    .endDate(LocalDate.now().plusDays(25))
                    .active(true)
                    .build());

            campaignRepository.save(Campaign.builder()
                    .name("Travel Bonanza 2026")
                    .description("Special 5% holiday bonus cashback on flights and hotels")
                    .category("TRAVEL")
                    .merchant("ALL")
                    .bonusPercentage(new BigDecimal("5.00"))
                    .minTransactionAmount(new BigDecimal("2000.00"))
                    .maxReward(new BigDecimal("2000.00"))
                    .bonusPoints(500L)
                    .startDate(LocalDate.now().minusDays(2))
                    .endDate(LocalDate.now().plusDays(40))
                    .active(true)
                    .build());

            campaignRepository.save(Campaign.builder()
                    .name("Tech Carnival Amazon Special")
                    .description("Flat 3% booster on Electronics purchases on Amazon")
                    .category("ELECTRONICS")
                    .merchant("Amazon")
                    .bonusPercentage(new BigDecimal("3.00"))
                    .minTransactionAmount(new BigDecimal("1000.00"))
                    .maxReward(new BigDecimal("1000.00"))
                    .bonusPoints(250L)
                    .startDate(LocalDate.now().minusDays(10))
                    .endDate(LocalDate.now().plusDays(20))
                    .active(true)
                    .build());
        }

        // 3. Seed Users and Sample Transactions
        if (userRepository.count() == 0) {
            User user1 = userService.registerUser("Abhinand", "abhinand@rewardsbank.com", UserTier.GOLD);
            User user2 = userService.registerUser("Priya Sharma", "priya@example.com", UserTier.PLATINUM);
            User user3 = userService.registerUser("Rahul Verma", "rahul@example.com", UserTier.SILVER);

            // Initial transactions for Abhinand
            transactionService.createTransaction(user1.getId(), "Amazon", "ELECTRONICS", new BigDecimal("2000.00"), "Sony Wireless Headphones");
            transactionService.createTransaction(user1.getId(), "Swiggy", "DINING", new BigDecimal("800.00"), "Gourmet Dinner Order");
            transactionService.createTransaction(user1.getId(), "Shell Petrol", "FUEL", new BigDecimal("1500.00"), "Weekend Car Fuel");
            transactionService.createTransaction(user1.getId(), "XYZ Supermarket", "GROCERIES", new BigDecimal("5000.00"), "Monthly Grocery Basket");

            // Initial referrals for Abhinand
            referralRepository.save(Referral.builder()
                    .referrerUserId(user1.getId())
                    .referredUserId(user3.getId())
                    .referredUserName("Rahul Verma")
                    .referredUserEmail("rahul@example.com")
                    .status(ReferralStatus.COMPLETED)
                    .rewardPoints(500L)
                    .build());
            walletService.creditPoints(user1.getId(), 500L);

            referralRepository.save(Referral.builder()
                    .referrerUserId(user1.getId())
                    .referredUserName("Arjun Mehta")
                    .referredUserEmail("arjun@example.com")
                    .status(ReferralStatus.PENDING)
                    .rewardPoints(500L)
                    .build());

            // Transactions for Priya
            transactionService.createTransaction(user2.getId(), "MakeMyTrip", "TRAVEL", new BigDecimal("12000.00"), "Flight booking Mumbai to Delhi");
        }

        log.info("Seed data initialization completed successfully!");
    }
}
