package com.example.rewards.referral;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Referrals", description = "Customer referral tracking and referral reward distribution")
@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @Data
    public static class InviteFriendRequest {
        @NotNull(message = "referrerUserId is required")
        private Long referrerUserId;

        @NotBlank(message = "friendName is required")
        private String friendName;

        @Email(message = "Valid friendEmail is required")
        @NotBlank(message = "friendEmail is required")
        private String friendEmail;
    }

    @Operation(summary = "Get user referrals", description = "List referrals sent by a specific customer")
    @ApiResponse(responseCode = "200", description = "List of user referrals")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Referral>> getReferralsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(referralService.getReferralsByReferrer(userId));
    }

    @Operation(summary = "List all referrals", description = "Global list of all referral records")
    @ApiResponse(responseCode = "200", description = "All referrals")
    @GetMapping
    public ResponseEntity<List<Referral>> getAllReferrals() {
        return ResponseEntity.ok(referralService.getAllReferrals());
    }

    @Operation(summary = "Send referral invite", description = "Send an invite to a new friend from an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Referral invite sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request or already referred")
    })
    @PostMapping("/invite")
    public ResponseEntity<Referral> inviteFriend(@Valid @RequestBody InviteFriendRequest request) {
        return new ResponseEntity<>(
                referralService.inviteFriend(request.getReferrerUserId(), request.getFriendName(), request.getFriendEmail()),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Complete referral", description = "Mark referral completed after qualifying conditions and award reward points")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Referral completed and reward credited"),
            @ApiResponse(responseCode = "400", description = "Referral already completed"),
            @ApiResponse(responseCode = "404", description = "Referral not found")
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<Referral> completeReferral(@PathVariable Long id) {
        return ResponseEntity.ok(referralService.completeReferral(id));
    }
}
