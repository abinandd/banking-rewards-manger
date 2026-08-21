package com.example.rewards.referral;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referrer_user_id", nullable = false)
    private Long referrerUserId;

    @Column(name = "referred_user_id")
    private Long referredUserId;

    @Column(name = "referred_user_name", nullable = false)
    private String referredUserName;

    @Column(name = "referred_user_email", nullable = false)
    private String referredUserEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferralStatus status;

    @Column(name = "reward_points")
    private Long rewardPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ReferralStatus.PENDING;
        }
        if (rewardPoints == null) {
            rewardPoints = 500L;
        }
    }
}
