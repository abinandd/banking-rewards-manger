package com.example.rewards;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.referral.Referral;
import com.example.rewards.referral.ReferralRepository;
import com.example.rewards.referral.ReferralService;
import com.example.rewards.referral.ReferralStatus;
import com.example.rewards.user.User;
import com.example.rewards.user.UserRepository;
import com.example.rewards.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReferralServiceTest {

    @Mock
    private ReferralRepository referralRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletService walletService;

    @InjectMocks
    private ReferralService referralService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Successfully invite a friend")
    void testInviteFriend() {
        User referrer = User.builder().id(1L).name("Abhinand").email("abhinand@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));
        when(referralRepository.save(any(Referral.class))).thenAnswer(i -> {
            Referral ref = (Referral) i.getArguments()[0];
            ref.setId(10L);
            return ref;
        });

        Referral referral = referralService.inviteFriend(1L, "Friend One", "friend@test.com");

        assertNotNull(referral);
        assertEquals("friend@test.com", referral.getReferredUserEmail());
        assertEquals(ReferralStatus.PENDING, referral.getStatus());
        assertEquals(500L, referral.getRewardPoints());
    }

    @Test
    @DisplayName("Prevent self-referral")
    void testPreventSelfReferral() {
        User referrer = User.builder().id(1L).name("Abhinand").email("abhinand@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));

        assertThrows(BusinessException.class, () ->
                referralService.inviteFriend(1L, "Self", "abhinand@test.com"));
    }

    @Test
    @DisplayName("Complete referral and award points")
    void testCompleteReferral() {
        Referral pendingRef = Referral.builder()
                .id(10L)
                .referrerUserId(1L)
                .referredUserName("Friend One")
                .referredUserEmail("friend@test.com")
                .status(ReferralStatus.PENDING)
                .rewardPoints(500L)
                .build();

        when(referralRepository.findById(10L)).thenReturn(Optional.of(pendingRef));
        when(referralRepository.save(any(Referral.class))).thenAnswer(i -> i.getArguments()[0]);

        Referral completed = referralService.completeReferral(10L);

        assertEquals(ReferralStatus.COMPLETED, completed.getStatus());
        verify(walletService, times(1)).creditPoints(1L, 500L);
    }
}
