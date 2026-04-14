package com.marketplace.user.application;

import com.marketplace.shared.exception.BusinessException;
import com.marketplace.user.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("User not found"));
        return UserDto.from(user);
    }

    @Transactional
    public UserDto updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));
        user.updateProfile(request.firstName(), request.lastName(), request.phone(), request.avatarUrl());
        user = userRepository.save(user);
        return UserDto.from(user);
    }

    @Transactional
    public void verifyEmail(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));
        user.verifyEmail();
        userRepository.save(user);
    }

    @Transactional
    public void verifyPhone(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));
        user.verifyPhone();
        userRepository.save(user);
    }

    @Transactional
    public void onboardProvider(UUID userId, String businessName, String category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));
        if (!user.isProvider()) {
            throw new BusinessException("User is not a provider");
        }
        user.registerEvent(new ProviderOnboardedEvent(userId, businessName, category));
        userRepository.save(user);
        log.info("Provider onboarded: userId={}, businessName={}", userId, businessName);
    }
}
