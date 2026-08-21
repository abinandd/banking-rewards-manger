package com.example.rewards;

import com.example.rewards.reward.Reward;
import com.example.rewards.reward.RewardRepository;
import com.example.rewards.reward.RewardStatus;
import com.example.rewards.reward.RewardType;
import com.example.rewards.transaction.Transaction;
import com.example.rewards.transaction.TransactionService;
import com.example.rewards.transaction.TransactionStatus;
import com.example.rewards.user.User;
import com.example.rewards.user.UserService;
import com.example.rewards.user.UserTier;
import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BankingRewardsIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private RewardRepository rewardRepository;

    @Test
    @DisplayName("Test 1: User Registration, Transaction creation, Reward Engine multi-rule calculation, and Wallet Credit")
    void testTransactionRewardFlow() {
        // 1. Create a Gold Tier User
        User user = userService.registerUser("Vikram Test", "vikram.test@example.com", UserTier.GOLD);
        assertNotNull(user.getId());
        assertEquals(UserTier.GOLD, user.getTier());

        // 2. Perform a ₹2,000 Electronics purchase on Amazon
        Transaction txn = transactionService.createTransaction(
                user.getId(),
                "Amazon",
                "ELECTRONICS",
                new BigDecimal("2000.00"),
                "Electronics test buy"
        );

        assertNotNull(txn.getId());
        assertEquals(TransactionStatus.COMPLETED, txn.getStatus());

        // 3. Verify Rewards Ledger
        List<Reward> rewards = rewardRepository.findByTransactionId(txn.getId());
        assertFalse(rewards.isEmpty());

        Reward cashbackReward = rewards.stream()
                .filter(r -> r.getType() == RewardType.CASHBACK)
                .findFirst()
                .orElse(null);

        assertNotNull(cashbackReward);
        assertEquals(RewardStatus.CREDITED, cashbackReward.getStatus());
        assertTrue(cashbackReward.getAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(cashbackReward.getBreakdownJson().contains("Base Cashback"));

        // 4. Verify Wallet balance updated
        RewardWallet wallet = walletService.getWalletByUserId(user.getId());
        assertEquals(cashbackReward.getAmount(), wallet.getCashbackBalance());
        assertTrue(wallet.getPointsBalance() > 0);
    }

    @Test
    @DisplayName("Test 2: Transaction Refund & Immutable Reward Reversal Audit Trail")
    void testRefundAndRewardReversalFlow() {
        // 1. Create User & Transaction
        User user = userService.registerUser("Anita Test", "anita.test@example.com", UserTier.SILVER);
        Transaction txn = transactionService.createTransaction(
                user.getId(),
                "Swiggy",
                "DINING",
                new BigDecimal("1000.00"),
                "Dinner order"
        );

        RewardWallet walletBeforeRefund = walletService.getWalletByUserId(user.getId());
        BigDecimal creditedCashback = walletBeforeRefund.getCashbackBalance();

        assertTrue(creditedCashback.compareTo(BigDecimal.ZERO) > 0);

        // 2. Perform Refund
        Transaction refundedTxn = transactionService.refundTransaction(txn.getId());
        assertEquals(TransactionStatus.REFUNDED, refundedTxn.getStatus());

        // 3. Check Ledger Audit Trail: An explicit REVERSED record must exist
        List<Reward> rewards = rewardRepository.findByTransactionId(txn.getId());
        boolean hasReversalRecord = rewards.stream().anyMatch(r ->
                r.getStatus() == RewardStatus.REVERSED && r.getAmount().compareTo(BigDecimal.ZERO) < 0);

        assertTrue(hasReversalRecord, "Should contain negative reversal ledger record");

        // 4. Verify Wallet balance deducted back to 0
        RewardWallet walletAfterRefund = walletService.getWalletByUserId(user.getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(walletAfterRefund.getCashbackBalance()));
        assertEquals(0L, walletAfterRefund.getPointsBalance());
    }

    @Test
    @DisplayName("Test 3: Prevent duplicate refunding")
    void testPreventDuplicateRefund() {
        User user = userService.registerUser("Siddharth Test", "siddharth@example.com", UserTier.SILVER);
        Transaction txn = transactionService.createTransaction(
                user.getId(),
                "Shell Petrol",
                "FUEL",
                new BigDecimal("500.00"),
                "Fuel refill"
        );

        transactionService.refundTransaction(txn.getId());

        // Trying to refund again should throw RuntimeException
        assertThrows(RuntimeException.class, () -> transactionService.refundTransaction(txn.getId()));
    }
}
