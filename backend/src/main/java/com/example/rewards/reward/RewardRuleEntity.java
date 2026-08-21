package com.example.rewards.reward;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reward_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchant; // e.g., "Amazon", "Swiggy", or null/ALL for any

    private String category; // e.g., "ELECTRONICS", "GROCERIES", "DINING", "TRAVEL", "FUEL", or ALL

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Column(name = "reward_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal rewardValue; // percentage (e.g. 1.5 for 1.5%) or fixed multiplier

    @Column(name = "max_reward", precision = 12, scale = 2)
    private BigDecimal maxReward; // cap in INR

    @Column(name = "min_spend", precision = 12, scale = 2)
    private BigDecimal minSpend;

    @Column(nullable = false)
    private boolean active;
}
