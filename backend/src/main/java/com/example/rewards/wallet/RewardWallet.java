package com.example.rewards.wallet;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "cashback_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal cashbackBalance;

    @Column(name = "points_balance", nullable = false)
    private Long pointsBalance;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (cashbackBalance == null) {
            cashbackBalance = BigDecimal.ZERO;
        }
        if (pointsBalance == null) {
            pointsBalance = 0L;
        }
        updatedAt = LocalDateTime.now();
    }
}
