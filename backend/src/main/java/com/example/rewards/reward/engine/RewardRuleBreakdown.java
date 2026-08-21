package com.example.rewards.reward.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRuleBreakdown {
    private String ruleName;
    private String description;
    private BigDecimal percentage;
    private BigDecimal calculatedAmount;
    private Long calculatedPoints;
}
