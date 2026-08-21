package com.example.rewards.campaign;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Campaigns", description = "Time-bound promotional campaigns and booster offers")
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @Operation(summary = "List campaigns", description = "Retrieve all campaigns or filter active ones")
    @ApiResponse(responseCode = "200", description = "List of promotional campaigns")
    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(campaignService.getActiveCampaigns());
        }
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @Operation(summary = "Get campaign by ID", description = "Fetch single campaign details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign found"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @Operation(summary = "Create campaign", description = "Publish a new marketing campaign booster")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Campaign created"),
            @ApiResponse(responseCode = "400", description = "Invalid campaign payload")
    })
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody Campaign campaign) {
        return new ResponseEntity<>(campaignService.createCampaign(campaign), HttpStatus.CREATED);
    }

    @Operation(summary = "Toggle campaign active state", description = "Activate or deactivate a promotional campaign")
    @ApiResponse(responseCode = "200", description = "Campaign state changed")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Campaign> toggleCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.toggleCampaignStatus(id));
    }

    @Operation(summary = "Delete campaign", description = "Remove a campaign by ID")
    @ApiResponse(responseCode = "204", description = "Campaign deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
