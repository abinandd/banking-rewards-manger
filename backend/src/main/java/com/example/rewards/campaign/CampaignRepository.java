package com.example.rewards.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByActiveTrue();

    @Query("SELECT c FROM Campaign c WHERE c.active = true AND c.startDate <= :today AND c.endDate >= :today")
    List<Campaign> findActiveCampaignsForDate(@Param("today") LocalDate today);
}
