package com.example.rewards.referral;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.user.User;
import com.example.rewards.user.UserRepository;
import com.example.rewards.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferralServiceTest {

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private ReferralService referralService;

    private User referrer;

    @BeforeEach
    void setUp() {
        referrer = User.builder()
                .id(1L)
                .name("Abhinand")
                .email("abhinand@rewardsbank.com")
                .build();
    }

    @Test
    void inviteFriend_ValidData_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));
        when(referralRepository.existsByReferrerUserIdAndReferredUserEmail(1L, "friend@example.com")).thenReturn(false);
        when(referralRepository.save(any(Referral.class))).thenAnswer(i -> {
            Referral ref = i.getArgument(0);
            ref.setId(50L);
            return ref;
        });

        Referral referral = referralService.inviteFriend(1L, "Friend", "friend@example.com");

        assertThat(referral.getId()).isEqualTo(50L);
        assertThat(referral.getStatus()).isEqualTo(ReferralStatus.PENDING);
        assertThat(referral.getRewardPoints()).isEqualTo(500L);
    }

    @Test
    void inviteFriend_SelfReferral_ThrowsBusinessException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));

        assertThatThrownBy(() -> referralService.inviteFriend(1L, "Myself", "abhinand@rewardsbank.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot refer yourself");
    }

    @Test
    void inviteFriend_DuplicateInvite_ThrowsBusinessException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));
        when(referralRepository.existsByReferrerUserIdAndReferredUserEmail(1L, "duplicate@example.com")).thenReturn(true);

        assertThatThrownBy(() -> referralService.inviteFriend(1L, "Friend", "duplicate@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already sent a referral invite");
    }

    @Test
    void completeReferral_PendingReferral_AwardsPoints() {
        Referral pending = Referral.builder()
                .id(20L)
                .referrerUserId(1L)
                .referredUserName("Friend")
                .referredUserEmail("friend@example.com")
                .status(ReferralStatus.PENDING)
                .rewardPoints(500L)
                .build();

        when(referralRepository.findById(20L)).thenReturn(Optional.of(pending));
        when(referralRepository.save(any(Referral.class))).thenAnswer(i -> i.getArgument(0));

        Referral completed = referralService.completeReferral(20L);

        assertThat(completed.getStatus()).isEqualTo(ReferralStatus.COMPLETED);
        verify(walletService).creditPoints(1L, 500L);
    }

    @Test
    void completeReferral_AlreadyCompleted_ThrowsBusinessException() {
        Referral alreadyCompleted = Referral.builder()
                .id(21L)
                .referrerUserId(1L)
                .status(ReferralStatus.COMPLETED)
                .build();

        when(referralRepository.findById(21L)).thenReturn(Optional.of(alreadyCompleted));

        assertThatThrownBy(() -> referralService.completeReferral(21L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already completed");

        verifyNoInteractions(walletService);
    }
}
