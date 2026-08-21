package com.example.rewards.referral;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @Data
    public static class InviteFriendRequest {
        private Long referrerUserId;
        @NotBlank
        private String friendName;
        @Email
        @NotBlank
        private String friendEmail;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Referral>> getReferralsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(referralService.getReferralsByReferrer(userId));
    }

    @GetMapping
    public ResponseEntity<List<Referral>> getAllReferrals() {
        return ResponseEntity.ok(referralService.getAllReferrals());
    }

    @PostMapping("/invite")
    public ResponseEntity<Referral> inviteFriend(@Valid @RequestBody InviteFriendRequest request) {
        return new ResponseEntity<>(
                referralService.inviteFriend(request.getReferrerUserId(), request.getFriendName(), request.getFriendEmail()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Referral> completeReferral(@PathVariable Long id) {
        return ResponseEntity.ok(referralService.completeReferral(id));
    }
}
