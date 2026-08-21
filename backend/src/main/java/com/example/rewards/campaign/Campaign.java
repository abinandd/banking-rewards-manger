package com.example.rewards.campaign;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String category; // e.g. "GROCERIES", "TRAVEL", "ELECTRONICS", "ALL"

    private String merchant; // e.g. "Amazon", "Swiggy", "ALL"

    @Column(name = "bonus_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal bonusPercentage;

    @Column(name = "min_transaction_amount", precision = 12, scale = 2)
    private BigDecimal minTransactionAmount;

    @Column(name = "max_reward", precision = 12, scale = 2)
    private BigDecimal maxReward;

    @Column(name = "bonus_points")
    private Long bonusPoints;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active;
}
