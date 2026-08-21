package com.example.rewards.reward;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewards", indexes = {
        @Index(name = "idx_rewards_user_id", columnList = "user_id"),
        @Index(name = "idx_rewards_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_rewards_status", columnList = "status"),
        @Index(name = "idx_rewards_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount; // in INR for CASHBACK

    @Column(name = "points")
    private Long points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus status;

    private String description;

    @Column(name = "breakdown_json", columnDefinition = "TEXT")
    private String breakdownJson; // Detailed rule calculation breakdown JSON for UI inspection

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RewardStatus.CREDITED;
        }
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (points == null) {
            points = 0L;
        }
    }
}
