package com.example.rewards.campaign;

import com.example.rewards.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public List<Campaign> getActiveCampaigns() {
        LocalDate today = LocalDate.now();
        return campaignRepository.findByActiveTrue().stream()
                .filter(c -> !today.isBefore(c.getStartDate()) && !today.isAfter(c.getEndDate()))
                .toList();
    }

    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    @Transactional
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
    public Campaign toggleCampaignStatus(Long id) {
        Campaign campaign = getCampaignById(id);
        campaign.setActive(!campaign.isActive());
        return campaignRepository.save(campaign);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
