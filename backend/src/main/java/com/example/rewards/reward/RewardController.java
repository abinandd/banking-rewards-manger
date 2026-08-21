package com.example.rewards.reward;

import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final WalletService walletService;

    @GetMapping("/users/{userId}/rewards")
    public ResponseEntity<List<Reward>> getUserRewards(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUserId(userId));
    }

    @GetMapping("/users/{userId}/rewards/balance")
    public ResponseEntity<RewardWallet> getUserWalletBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getOrCreateWallet(userId));
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @GetMapping("/rewards/rules")
    public ResponseEntity<List<RewardRuleEntity>> getAllRules() {
        return ResponseEntity.ok(rewardService.getAllRules());
    }

    @PostMapping("/rewards/rules")
    public ResponseEntity<RewardRuleEntity> createRule(@Valid @RequestBody RewardRuleEntity rule) {
        return new ResponseEntity<>(rewardService.createRule(rule), HttpStatus.CREATED);
    }

    @PatchMapping("/rewards/rules/{id}/toggle")
    public ResponseEntity<RewardRuleEntity> toggleRule(@PathVariable Long id) {
        return ResponseEntity.ok(rewardService.toggleRule(id));
    }

    @DeleteMapping("/rewards/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        rewardService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
