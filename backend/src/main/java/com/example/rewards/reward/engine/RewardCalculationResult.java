package com.example.rewards.reward.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardCalculationResult {
    @Builder.Default
    private BigDecimal totalCashback = BigDecimal.ZERO;
    @Builder.Default
    private Long totalPoints = 0L;
    @Builder.Default
    private List<RewardRuleBreakdown> breakdowns = new ArrayList<>();

    public void addCashback(String ruleName, String description, BigDecimal percentage, BigDecimal amount) {
        this.totalCashback = this.totalCashback.add(amount);
        this.breakdowns.add(RewardRuleBreakdown.builder()
                .ruleName(ruleName)
                .description(description)
                .percentage(percentage)
                .calculatedAmount(amount)
                .calculatedPoints(0L)
                .build());
    }

    public void addPoints(String ruleName, String description, Long points) {
        this.totalPoints += points;
        this.breakdowns.add(RewardRuleBreakdown.builder()
                .ruleName(ruleName)
                .description(description)
                .percentage(BigDecimal.ZERO)
                .calculatedAmount(BigDecimal.ZERO)
                .calculatedPoints(points)
                .build());
    }
}
