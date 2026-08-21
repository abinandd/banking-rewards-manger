package com.example.rewards.reward;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Reward> findByTransactionId(Long transactionId);
    Optional<Reward> findFirstByTransactionIdAndStatus(Long transactionId, RewardStatus status);
}
