package com.example.rewards.referral;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import com.example.rewards.user.User;
import com.example.rewards.user.UserRepository;
import com.example.rewards.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    public List<Referral> getReferralsByReferrer(Long referrerId) {
        return referralRepository.findByReferrerUserIdOrderByCreatedAtDesc(referrerId);
    }

    public List<Referral> getAllReferrals() {
        return referralRepository.findAll();
    }

    @Transactional
    public Referral inviteFriend(Long referrerId, String friendName, String friendEmail) {
        User referrer = userRepository.findById(referrerId)
                .orElseThrow(() -> new ResourceNotFoundException("Referrer user not found: " + referrerId));

        Referral referral = Referral.builder()
                .referrerUserId(referrerId)
                .referredUserName(friendName)
                .referredUserEmail(friendEmail)
                .status(ReferralStatus.PENDING)
                .rewardPoints(500L)
                .build();

        return referralRepository.save(referral);
    }

    @Transactional
    public Referral completeReferral(Long referralId) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found: " + referralId));

        if (referral.getStatus() == ReferralStatus.COMPLETED) {
            throw new BusinessException("Referral is already completed");
        }

        referral.setStatus(ReferralStatus.COMPLETED);
        walletService.creditPoints(referral.getReferrerUserId(), referral.getRewardPoints());

        return referralRepository.save(referral);
    }
}
