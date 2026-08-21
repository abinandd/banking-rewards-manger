package com.example.rewards.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RewardRuleRepository extends JpaRepository<RewardRuleEntity, Long> {
    List<RewardRuleEntity> findByActiveTrue();
}
