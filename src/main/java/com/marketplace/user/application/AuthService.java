package com.marketplace.user.application;

import com.marketplace.shared.exception.BusinessException;
import com.marketplace.shared.security.CustomUserDetails;
import com.marketplace.shared.security.jwt.JwtTokenProvider;
import com.marketplace.user.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phone(request.phone())
            .role(request.role())
            .status(UserStatus.PENDING_VERIFICATION)
            .build();

        user = userRepository.save(user);
        user.activate();
        user = userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        log.info("User registered: {}", user.getEmail());
        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        user.recordLogin();
        userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }
        UUID userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found"));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(newAccessToken, newRefreshToken, user.getId(), user.getEmail(), user.getRole().name());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getRole().name());
    }
}
