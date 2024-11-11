package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.dto.TokenRequest;
import com.ci.Cruming.auth.dto.TokenResponse;
import com.ci.Cruming.auth.dto.UserProfile;
import com.ci.Cruming.user.entity.Platform;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.user.repository.UserRepository;
import com.ci.Cruming.auth.service.validator.AbstractTokenValidator;
import com.ci.Cruming.auth.service.validator.TokenValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final TokenValidatorFactory tokenValidatorFactory;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse exchangeToken(TokenRequest request) {
        Platform platform = Platform.valueOf(request.provider().toUpperCase());
        AbstractTokenValidator validator = tokenValidatorFactory.getValidator(platform);
        
        UserProfile profile = validator.validateAndGetProfile(request.socialToken());
        System.out.println("profile = " + profile);
        User user = userRepository.findByPlatformAndPlatformId(
                platform,
                profile.getPlatformId()
            )
            .orElseGet(() -> createUser(profile, platform));

        return jwtTokenProvider.createToken(user);
    }

    private User createUser(UserProfile profile, Platform platform) {
        User user = User.builder()
            .nickname(profile.getNickname())
            .platform(platform)
            .platformId(profile.getPlatformId())
            .build();
        System.out.println("user = " + user);
        return userRepository.save(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return jwtTokenProvider.createToken(user);
    }
} 