package com.example.rewards.user;

import com.example.rewards.common.exception.BusinessException;
import com.example.rewards.common.exception.ResourceNotFoundException;
import com.example.rewards.wallet.RewardWallet;
import com.example.rewards.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;

    @Transactional
    public User registerUser(String name, String email, UserTier tier) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("User already exists with email: " + email);
        }

        String referralCode = "REW" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        User user = User.builder()
                .name(name)
                .email(email)
                .tier(tier != null ? tier : UserTier.SILVER)
                .referralCode(referralCode)
                .build();

        User saved = userRepository.save(user);
        walletService.getOrCreateWallet(saved.getId());
        return saved;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserTier(Long id, UserTier newTier) {
        User user = getUserById(id);
        user.setTier(newTier);
        return userRepository.save(user);
    }
}
