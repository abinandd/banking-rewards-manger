package com.example.rewards.reward.engine;

import com.example.rewards.transaction.Transaction;
import com.example.rewards.user.User;

public interface RewardRule {
    String getRuleName();
    void evaluate(Transaction transaction, User user, RewardCalculationResult result);
}
