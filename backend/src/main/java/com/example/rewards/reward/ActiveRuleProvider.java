package com.example.rewards.reward;

import com.example.rewards.common.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Thin cacheable facade over RewardRuleRepository so that all reward engine rules
 * can retrieve the active rule list from cache rather than hitting the DB once per rule per transaction.
 */
@Component
@RequiredArgsConstructor
public class ActiveRuleProvider {

    private final RewardRuleRepository rewardRuleRepository;

    @Cacheable(value = CacheConfig.ACTIVE_RULES_CACHE)
    public List<RewardRuleEntity> getActiveRules() {
        return rewardRuleRepository.findByActiveTrue();
    }

    @CacheEvict(value = CacheConfig.ACTIVE_RULES_CACHE, allEntries = true)
    public void evictCache() {
        // triggered externally by RewardService after mutations
    }
}
