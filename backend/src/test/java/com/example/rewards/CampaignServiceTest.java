package com.example.rewards;

import com.example.rewards.campaign.Campaign;
import com.example.rewards.campaign.CampaignRepository;
import com.example.rewards.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Toggle campaign status correctly")
    void testToggleCampaignStatus() {
        Campaign campaign = Campaign.builder()
                .id(1L)
                .name("Summer Travel")
                .active(true)
                .build();

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(i -> i.getArguments()[0]);

        Campaign toggled = campaignService.toggleCampaignStatus(1L);
        assertFalse(toggled.isActive());

        Campaign toggledBack = campaignService.toggleCampaignStatus(1L);
        assertTrue(toggledBack.isActive());
    }

    @Test
    @DisplayName("Filter active campaigns by date range")
    void testGetActiveCampaigns() {
        LocalDate today = LocalDate.now();

        Campaign activeCampaign = Campaign.builder()
                .id(1L)
                .name("Active One")
                .startDate(today.minusDays(5))
                .endDate(today.plusDays(5))
                .active(true)
                .build();

        Campaign futureCampaign = Campaign.builder()
                .id(2L)
                .name("Future One")
                .startDate(today.plusDays(5))
                .endDate(today.plusDays(15))
                .active(true)
                .build();

        when(campaignRepository.findByActiveTrue()).thenReturn(List.of(activeCampaign, futureCampaign));

        List<Campaign> active = campaignService.getActiveCampaigns();
        assertEquals(1, active.size());
        assertEquals("Active One", active.get(0).getName());
    }
}
