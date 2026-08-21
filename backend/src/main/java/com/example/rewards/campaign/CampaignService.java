package com.example.rewards.campaign;

import com.example.rewards.common.config.CacheConfig;
import com.example.rewards.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Cacheable(value = CacheConfig.ACTIVE_CAMPAIGNS_CACHE)
    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findActiveCampaignsForDate(LocalDate.now());
    }

    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_CAMPAIGNS_CACHE, allEntries = true)
    public Campaign createCampaign(Campaign campaign) {
        if (campaign.getStartDate() == null) {
            campaign.setStartDate(LocalDate.now());
        }
        if (campaign.getEndDate() == null) {
            campaign.setEndDate(LocalDate.now().plusMonths(1));
        }
        return campaignRepository.save(campaign);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_CAMPAIGNS_CACHE, allEntries = true)
    public Campaign toggleCampaignStatus(Long id) {
        Campaign campaign = getCampaignById(id);
        campaign.setActive(!campaign.isActive());
        return campaignRepository.save(campaign);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.ACTIVE_CAMPAIGNS_CACHE, allEntries = true)
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
