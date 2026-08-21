package com.example.rewards.reward;

import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rewards & Rules", description = "Reward ledger entries, wallet balance queries, and dynamic reward rule administration")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final WalletService walletService;

    @Operation(summary = "Get user rewards ledger", description = "Fetch audit reward records and breakdown for a given customer")
    @ApiResponse(responseCode = "200", description = "User rewards list")
    @GetMapping("/users/{userId}/rewards")
    public ResponseEntity<List<Reward>> getUserRewards(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUserId(userId));
    }

    @Operation(summary = "Get user wallet balance", description = "Fetch current cashback and points balance for a given customer")
    @ApiResponse(responseCode = "200", description = "User wallet balance")
    @GetMapping("/users/{userId}/rewards/balance")
    public ResponseEntity<RewardWallet> getUserWalletBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getOrCreateWallet(userId));
    }

    @Operation(summary = "List all rewards ledger", description = "Retrieve global reward audit entries")
    @ApiResponse(responseCode = "200", description = "All reward ledger items")
    @GetMapping("/rewards")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @Operation(summary = "List reward rules", description = "Fetch all configurable business reward rules")
    @ApiResponse(responseCode = "200", description = "List of reward rules")
    @GetMapping("/rewards/rules")
    public ResponseEntity<List<RewardRuleEntity>> getAllRules() {
        return ResponseEntity.ok(rewardService.getAllRules());
    }

    @Operation(summary = "Create reward rule", description = "Register a new dynamic reward calculation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rule created"),
            @ApiResponse(responseCode = "400", description = "Invalid rule payload")
    })
    @PostMapping("/rewards/rules")
    public ResponseEntity<RewardRuleEntity> createRule(@Valid @RequestBody RewardRuleEntity rule) {
        return new ResponseEntity<>(rewardService.createRule(rule), HttpStatus.CREATED);
    }

    @Operation(summary = "Toggle rule status", description = "Enable or disable an existing reward rule")
    @ApiResponse(responseCode = "200", description = "Rule status toggled")
    @PatchMapping("/rewards/rules/{id}/toggle")
    public ResponseEntity<RewardRuleEntity> toggleRule(@PathVariable Long id) {
        return ResponseEntity.ok(rewardService.toggleRule(id));
    }

    @Operation(summary = "Delete reward rule", description = "Remove a rule by ID")
    @ApiResponse(responseCode = "204", description = "Rule deleted")
    @DeleteMapping("/rewards/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        rewardService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
